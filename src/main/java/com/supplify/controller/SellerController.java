package com.supplify.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import com.supplify.dto.SellerDto;
import com.supplify.entity.Seller;
import com.supplify.services.SellerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Controller
public class SellerController {
	private static final Logger logger = LoggerFactory.getLogger(SellerController.class);
	@Autowired
	private final SellerService sellerService;

	public SellerController(SellerService userService) {
		this.sellerService = userService;
	}

	
	@GetMapping("/sellerdashboard")
	public String showDashboard(Model model, Principal principal) {
	    try {
	        if (principal == null) {
	            return "redirect:/login";
	        }

	        Seller seller = sellerService.findSellerByEmail(principal.getName());
	        
	        if (seller == null) {
	            logger.error("No seller found for user: {}", principal.getName());
	            return "redirect:/login";
	        }

	        model.addAttribute("seller", seller);
	        return "sellerdashboard";
	    } catch (Exception e) {
	        logger.error("Error in dashboard access", e);
	        return "redirect:/error";
	    }
	}
	@GetMapping("/dashboard")
	public String dashboard() {
		logger.info("Accessing login page");
		return "dashboard"; 
	}
    
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new SellerDto());
		return "register"; 
	}

	@PostMapping("/register/save")
	public String registration(
	    @RequestParam("images") MultipartFile[] images,
	    @Valid @ModelAttribute("user") SellerDto userDto, 
	    BindingResult result, 
	    Model model) {

	    logger.info("Received SellerDto: {}", userDto);

	    Seller existingUser = sellerService.findSellerByEmail(userDto.getEmail());
	    if (existingUser != null) {
	        result.rejectValue("email", null, "An account with this email already exists");
	    }

	    if (result.hasErrors()) {
	        logger.error("Validation errors: {}", result.getAllErrors());
	        model.addAttribute("user", userDto);
	        return "register";
	    }

	    if (!userDto.isPasswordMatching()) {
	        result.rejectValue("confirmpassword", null, "Passwords do not match");
	        model.addAttribute("user", userDto);
	        return "register";
	    }

	    try {

	        sellerService.saveUser(userDto, images);
	    } catch (Exception e) {
	        logger.error("Error saving user", e);
	        model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
	        model.addAttribute("user", userDto);
	        return "register";
	    }
		model.addAttribute("successMessage", "Your registration is pending approval by the admin.");
	    return "redirect:/login";
	}
	@GetMapping("/seller_profile")
	public String viewProfile(Model model, Principal principal) {
	    try {
	        if (principal == null) {
	            return "redirect:/login";
	        }

	        Seller seller = sellerService.findSellerByEmail(principal.getName());
	        
	        if (seller == null) {
	            logger.error("No seller found for user: {}", principal.getName());
	            return "redirect:/login";
	        }

	        logger.info("Seller Image URLs: {}", seller.getImageUrls());

	        model.addAttribute("seller", seller);
	        model.addAttribute("imageUrls", seller.getImageUrls());
	        
	        return "seller_profile";
	    } catch (Exception e) {
	        logger.error("Error in profile access", e);
	        return "redirect:/error";
	    }
	}	
	
	@GetMapping("/edit_profile")
	public String showEditProfileForm(Model model, Principal principal) {
	    try {
	        if (principal == null) {
	            return "redirect:/login";
	        }

	        Seller seller = sellerService.findSellerByEmail(principal.getName());
	        
	        if (seller == null) {
	            logger.error("No seller found for user: {}", principal.getName());
	            return "redirect:/login";
	        }

	        SellerDto sellerDto = new SellerDto();
	        String[] nameParts = seller.getName().split(" ", 2);
	        sellerDto.setFirstName(nameParts[0]);
	        sellerDto.setLastName(nameParts.length > 1 ? nameParts[1] : "");
	        sellerDto.setEmail(seller.getEmail());
	        sellerDto.setPhone(seller.getPhone());
	        sellerDto.setShopName(seller.getShopname());
	        sellerDto.setAddress(seller.getAddress());
	        sellerDto.setZipcode(seller.getZipcode());

	        model.addAttribute("user", sellerDto);
	        model.addAttribute("seller", seller);

	        return "edit_profile"; 
	    } catch (Exception e) {
	        logger.error("Error in edit profile access", e);
	        return "redirect:/error";
	    }
	}
	@PostMapping("/seller/update")
	public String updateProfile(
	    @RequestParam("images") MultipartFile[] images,
	    @Valid @ModelAttribute("user") SellerDto userDto, 
	    BindingResult result, 
	    Model model,
	    Principal principal) {

	    if (result.hasErrors()) {
	        logger.error("Validation errors: {}", result.getAllErrors());
	        
	        Seller existingSeller = sellerService.findSellerByEmail(principal.getName());
	        model.addAttribute("seller", existingSeller);
	        
	        model.addAttribute("user", userDto);
	        return "edit_profile";
	    }

	    if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
	        if (!userDto.isPasswordMatching()) {
	            Seller existingSeller = sellerService.findSellerByEmail(principal.getName());
	            model.addAttribute("seller", existingSeller);
	            
	            result.rejectValue("confirmpassword", null, "Passwords do not match");
	            model.addAttribute("user", userDto);
	            return "edit_profile";
	        }
	    }

	    try {
	        sellerService.updateSellerProfile(userDto, images);
	        return "redirect:/seller_profile";
	    } catch (Exception e) {
	        logger.error("Error updating profile", e);
	        
	        Seller existingSeller = sellerService.findSellerByEmail(principal.getName());
	        model.addAttribute("seller", existingSeller);
	        
	        model.addAttribute("errorMessage", "Profile update failed: " + e.getMessage());
	        model.addAttribute("user", userDto);
	        return "edit_profile";
	    }
	}
	@GetMapping("/users")
	public String users(Model model) {
		List<SellerDto> sellers = sellerService.findAllUsers();
		model.addAttribute("users", sellers);
		return "users"; 
	}
 
	
	  @GetMapping(value = "/logout")
	  
	  @ResponseStatus(value = HttpStatus.OK) 
	  public void logout(HttpServletRequest
	  request) { HttpSession session = request.getSession(false); if (session !=
	  null) { session.invalidate();  }}
	 
}

