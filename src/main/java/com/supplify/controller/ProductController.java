package com.supplify.controller;

import com.supplify.entity.Product;
import com.supplify.entity.Seller;
import com.supplify.repository.SellerRepository;
import com.supplify.service.Implement.ProductServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/inventory")
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private SellerRepository sellerRepository;

    /**
     * List products for the logged-in seller
     */
    @GetMapping
    public String listProducts(
        @RequestParam(value = "keyword", required = false) String keyword, 
        Model model
    ) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve seller
            Seller seller = sellerRepository.findByEmail(username);
            if (seller == null) {
                logger.error("No seller found for username: {}", username);
                return "redirect:/login";
            }

            // Retrieve products
            List<Product> products;
            if (keyword != null && !keyword.isEmpty()) {
                products = productService.searchProductsBySellerAndKeyword(seller, keyword);
            } else {
                products = productService.getProductsBySeller(seller);
            }

            // Add attributes to model
            model.addAttribute("seller", seller);
            model.addAttribute("products", products);
            model.addAttribute("keyword", keyword != null ? keyword : "");

            return "inventory";
        } catch (Exception e) {
            logger.error("Error retrieving inventory", e);
            return "redirect:/error";
        }
    }

    /**
     * Show add product form
     */
    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve seller
            Seller seller = sellerRepository.findByEmail(username);
            if (seller == null) {
                logger.error("No seller found for username: {}", username);
                return "redirect:/login";
            }

            // Prepare new product
            Product product = new Product();
            product.setSeller(seller);

            model.addAttribute("product", product);
            model.addAttribute("seller", seller);

            return "add_product";
        } catch (Exception e) {
            logger.error("Error preparing add product form", e);
            return "redirect:/error";
        }
    }

    /**
     * Add new product
     */
    @PostMapping("/add")
    public String addProduct(
        @Valid @ModelAttribute Product product, 
        BindingResult bindingResult,
        @RequestParam("images") MultipartFile[] images,
        RedirectAttributes redirectAttributes
    ) {
        try {
            // Validate input
            if (bindingResult.hasErrors()) {
                // Log validation errors
                bindingResult.getFieldErrors().forEach(error -> 
                    logger.warn("Validation error: {} - {}", 
                        error.getField(), error.getDefaultMessage())
                );
                return "add_product";
            }

            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve seller
            Seller seller = sellerRepository.findByEmail(username);
            if (seller == null) {
                logger.error("No seller found for username: {}", username);
                return "redirect:/login";
            }

            // Set seller for the product
            product.setSeller(seller);

            // Add product
            productService.addProduct(product, images);

            // Add success message
            redirectAttributes.addFlashAttribute("successMessage", "Product added successfully!");
            return "redirect:/inventory";
        } catch (Exception e) {
            logger.error("Error adding product", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to add product: " + e.getMessage());
            return "redirect:/inventory/add";
        }
    }

    /**
     * Show edit product form
     */
    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable Long id, Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve seller
            Seller seller = sellerRepository.findByEmail(username);
            if (seller == null) {
                logger.error("No seller found for username: {}", username);
                return "redirect:/login";
            }

            // Retrieve product
            Product product = productService.getProductById(id);

            // Verify product belongs to the seller
            if (!product.getSeller().getId().equals(seller.getId())) {
                logger.warn("Unauthorized access to product: {} by seller: {}", 
                    id, username);
                return "redirect:/inventory";
            }

            model.addAttribute("product", product);
            model.addAttribute("seller", seller);

            return "edit_product";
        } catch (Exception e) {
            logger.error("Error preparing edit product form", e);
            return "redirect:/error";
        }
    }

    /**
     * Update product
     */
    @PostMapping("/update/{id}")
    public String updateProduct(
        @PathVariable Long id, 
        @Valid @ModelAttribute Product product, 
        BindingResult bindingResult,
        @RequestParam("images") MultipartFile[] images,
        RedirectAttributes redirectAttributes
    ) {
        try {
            // Validate input
            if (bindingResult.hasErrors()) {
                // Log validation errors
                bindingResult.getFieldErrors().forEach(error -> 
                    logger.warn("Validation error: {} - {}", 
                        error.getField(), error.getDefaultMessage())
                );
                return "edit_product";
            }

            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve seller
            Seller seller = sellerRepository.findByEmail(username);
            if (seller == null) {
                logger.error("No seller found for username: {}", username);
                return "redirect:/login";
            }

            // Update product
            productService.updateProduct(id, product, images);

            // Add success message
            redirectAttributes.addFlashAttribute("successMessage", "Product updated successfully!");
            return "redirect:/inventory";
        } catch (Exception e) {
            logger.error("Error updating product", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to update product: " + e.getMessage());
            return "redirect:/inventory/edit/" + id;
        }
    }

    /**
     * Delete product
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(
        @PathVariable Long id, 
        RedirectAttributes redirectAttributes
    ) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve seller
            Seller seller = sellerRepository.findByEmail(username);
            if (seller == null) {
                logger.error("No seller found for username: {}", username);
                return "redirect:/login";
            }

            // Delete product
            productService.deleteProduct(id);

             // Add success message
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully!");
            return "redirect:/inventory";
        } catch (Exception e) {
            logger.error("Error deleting product", e);
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Failed to delete product: " + e.getMessage());
            return "redirect:/inventory";
        }
    }
}