package com.supplify.service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.supplify.entity.Product;
import com.supplify.entity.Seller;
import com.supplify.repository.ProductRepository;
import com.supplify.repository.SellerRepository;

import jakarta.transaction.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SellerRepository sellerRepository;

   
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }
   
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsBySeller(Seller seller) {
        return productRepository.findBySeller(seller); // Fetch products based on the seller
    }

    public List<Product> searchProductsBySellerAndKeyword(Seller seller, String keyword) {
        return productRepository.searchProductsBySellerAndKeyword(seller, keyword); // Search products for the seller with a keyword
    }


    public Product addProduct(Product product, MultipartFile[] images) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Seller seller = sellerRepository.findByEmail(username);
        if (seller == null) {
            throw new RuntimeException("Seller not found for email: " + username);
        }

        product.setSeller(seller);
        product.setCreatedAt(LocalDateTime.now());

        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = saveImage(image);
                    if (imageUrl != null) {
                        product.getImageUrls().add(imageUrl);
                    }
                }
            }
        }

        System.out.println("Final image URLs: " + product.getImageUrls());
        calculateDiscountedPrice(product);

        product.setStatus(product.getQuantity() > 0 ? "In Stock" : "Out of Stock");
        return productRepository.save(product);
    }


    public Product updateProduct(Long id, Product updatedProduct, MultipartFile[] images) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update basic fields
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setStatus(updatedProduct.getQuantity() > 0 ? "In Stock" : "Out of Stock");
        existingProduct.setCategory(updatedProduct.getCategory());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setDiscountPercentage(updatedProduct.getDiscountPercentage());

        boolean hasNewImages = images != null && images.length > 0 && images[0].getSize() > 0;

        if (hasNewImages) {
            existingProduct.getImageUrls().clear(); 
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = saveImage(image); 
                    existingProduct.getImageUrls().add(imageUrl); 
                }
            }
        }

        calculateDiscountedPrice(existingProduct);

        return productRepository.save(existingProduct);
    }


    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public String saveImage(MultipartFile image) {
        if (image.isEmpty()) {
            System.out.println("Image is empty");
            return null;
        }
        try {
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                System.out.println("Invalid image type: " + contentType);
                throw new IllegalArgumentException("Uploaded file is not an image");
            }
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path path = Paths.get("src/main/resources/static/uploads/" + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/uploads/" + fileName;
            System.out.println("Saved image URL: " + imageUrl);
            return imageUrl;
        } catch (IOException e) {
            System.out.println("Failed to store image: " + e.getMessage());
            throw new RuntimeException("Failed to store image", e);
        }
    }


    private void calculateDiscountedPrice(Product product) {
        if (product.getDiscountPercentage() != null && product.getDiscountPercentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = product.getPrice().multiply(product.getDiscountPercentage().divide(BigDecimal.valueOf(100)));
            product.setDiscountedPrice(product.getPrice().subtract(discountAmount));
        } else {
            product.setDiscountedPrice(product.getPrice());
        }
    }

    @Transactional
    public void processPurchase(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock available");
        }

        // Update product quantity
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        // TODO: Create an order record in the database (if needed)
    }
}
