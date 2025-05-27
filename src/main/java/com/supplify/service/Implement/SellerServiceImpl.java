package com.supplify.service.Implement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.supplify.dto.SellerDto;
import com.supplify.entity.Role;
import com.supplify.entity.Seller;
import com.supplify.repository.RoleRepository;
import com.supplify.repository.SellerRepository;
import com.supplify.services.SellerService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SellerServiceImpl implements SellerService {

	@Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
   

    public SellerServiceImpl(SellerRepository sellerRepository, RoleRepository roleRepository, @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder) {
        this.sellerRepository = sellerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveUser(SellerDto sellerDto, MultipartFile[] images) throws IOException {
        Seller seller = new Seller();
        seller.setName(sellerDto.getFirstName() + " " + sellerDto.getLastName());
        seller.setEmail(sellerDto.getEmail());
        seller.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
        seller.setPhone(sellerDto.getPhone());
        seller.setShopname(sellerDto.getShopName());
        seller.setAddress(sellerDto.getAddress());
        
        seller.setZipcode(sellerDto.getZipcode());
        // shahid c5
        seller.setRejected(false);
        // shahid c5
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = saveImage(image);
                    if (imageUrl != null) {
                        seller.getImageUrls().add(imageUrl);
                    }
                }
            }
        }
        // Assign default role
        Role role = roleRepository.findByName("SELLER");
        if (role == null) {
            role = new Role();
            role.setName("SELLER");
            role = roleRepository.save(role);
        }

        seller.setRoles(Set.of(role));
        sellerRepository.save(seller);
    }

    @Override
    public Seller findSellerByEmail(String email) {
        return sellerRepository.findByEmail(email);
    }

    @Override
    public List<SellerDto> findAllUsers() {
        List<Seller> sellers = sellerRepository.findAll();
        return sellers.stream()
                .map((seller) -> mapToUserDto(seller))
                .collect(Collectors.toList());
    }

    private SellerDto mapToUserDto(Seller user){
        SellerDto sellerDto = new SellerDto();
        String[] str = sellerDto.getFirstName().split(" ");
        sellerDto.setFirstName(str[0]);
        sellerDto.setLastName(str[1]);
        sellerDto.setEmail(user.getEmail());
        return sellerDto;
    }

    private Role checkRoleExist(){
        Role role = new Role();
        role.setName("SELLER");
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void updateSellerProfile(SellerDto sellerDto, MultipartFile[] images) throws IOException {
        // Find existing seller by email
        Seller existingSeller = sellerRepository.findByEmail(sellerDto.getEmail());
        
        if (existingSeller == null) {
            throw new RuntimeException("Seller not found");
        }

        existingSeller.setName(sellerDto.getFirstName() + " " + sellerDto.getLastName());
        existingSeller.setEmail(sellerDto.getEmail());
        existingSeller.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
        sellerDto.setConfirmpassword(passwordEncoder.encode(sellerDto.getConfirmpassword()));
        existingSeller.setPhone(sellerDto.getPhone());
        existingSeller.setShopname(sellerDto.getShopName());
        existingSeller.setAddress(sellerDto.getAddress());
       
        existingSeller.setZipcode(sellerDto.getZipcode());

        if (sellerDto.getPassword() != null && !sellerDto.getPassword().isEmpty()) {
            existingSeller.setPassword(passwordEncoder.encode(sellerDto.getPassword()));
        }

        if (images != null && images.length > 0) {
            boolean hasValidImages = false;
            
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    hasValidImages = true;
                    break;
                }
            }

            if (hasValidImages) {
                existingSeller.getImageUrls().clear();
                
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String imageUrl = saveImage(image);
                        if (imageUrl != null) {
                            existingSeller.getImageUrls().add(imageUrl);
                        }
                    }
                }
            }
        }

        sellerRepository.save(existingSeller);
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
            Path path = Paths.get("src/main/resources/static/sellerDocumentImages/" + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/sellerDocumentImages/" + fileName;
            System.out.println("Saved image URL: " + imageUrl);
            return imageUrl;
        } catch (IOException e) {
            System.out.println("Failed to store image: " + e.getMessage());
            throw new RuntimeException("Failed to store image", e);
        }
    }

    
    
    public Seller getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName(); // Assuming the email is the username
            return findSellerByEmail(email);
        }
        return null; // Return null if no user is authenticated
    }

    //sahid c4
    @Override
    public Seller findById(UUID id) {
        return sellerRepository.findById(id).orElse(null);

    }

   public List<Seller> getPendingSellers() {
       return sellerRepository.findByIsApprovedFalseAndIsRejectedFalse();
   }

    public List<Seller> getRejectedSellers() {
        return sellerRepository.findByIsRejectedTrue();
    }

    @Override
    public List<Seller> getAllSellers() {

        return sellerRepository.findAllSellers();
    }

    public void approveSeller(UUID id) {
        Seller seller = sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("Seller not found"));
//        seller.setApproved(true);
        seller.setApproved(true);
        sellerRepository.save(seller);
    }



    public void rejectSeller(UUID id) {
        Seller seller = sellerRepository.findById(id).orElseThrow(() -> new RuntimeException("Seller not found"));
        seller.setRejected(true); // Set the rejected flag
        sellerRepository.save(seller);
    }
    //sahid c4
}