package com.supplify.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.supplify.dto.BuyerDto;
import com.supplify.enm.BuyerType;
import com.supplify.entity.Buyer;
import com.supplify.entity.Product;
import com.supplify.repository.BuyerRepository;
import com.supplify.service.Implement.ProductServiceImpl;
import com.supplify.services.BuyerService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class BuyerController {
	
	private static final Logger logger = LoggerFactory.getLogger(BuyerController.class);
	@Autowired
	private final BuyerService buyerService;
	@Autowired
	private ProductServiceImpl productServiceImpl;
	@Autowired
	private BuyerRepository buyerRepository;
	public BuyerController(BuyerService buyerService,ProductServiceImpl productServiceImpl,BuyerRepository buyerRepository) {
		this.buyerService=buyerService;
		this.productServiceImpl=productServiceImpl;
		this.buyerRepository=buyerRepository;
	}
	
	@GetMapping("/buyer")
	public String buyerRegis(Model model) {
	    model.addAttribute("buyerDto", new BuyerDto()); // Change to buyerDto
	    return "buyer"; // Ensure this returns the correct Thymeleaf template
	}
	
	
	@GetMapping("/buyerdashboard")
	public String showBuyerDashboard(
	        @AuthenticationPrincipal UserDetails userDetails,
	        Model model) {

	    if (userDetails == null) {
	        throw new IllegalStateException("UserDetails is null. Ensure the user is logged in.");
	    }

	    // Fetch buyer details based on the logged-in user's email
	    String email = userDetails.getUsername();
	    Buyer buyer = buyerService.findBuyerByEmail(email);

	    if (buyer == null) {
	        throw new IllegalStateException("Buyer not found for email: " + email);
	    }

	    String buyerType = buyer.getBuyerType().name(); // Assuming buyerType is an Enum

	    List<Product> allProducts = productServiceImpl.getAllProducts();
	    
	    List<Product> filteredProducts;

	    if (allProducts != null) {
	        // Filter products based on buyer type
	        if ("BULK_BUYER".equalsIgnoreCase(buyerType)) {
	            filteredProducts = allProducts.stream()
	                    .filter(product -> product.getQuantity() >= 200)
	                    .toList();
	        } else { // SMALL_BUYER
	            filteredProducts = allProducts.stream()
	                    .filter(product -> product.getQuantity() < 200)
	                    .toList();
	        }

	        // Debugging output
	        System.out.println("Buyer Type: " + buyerType);
	        for (Product product : filteredProducts) {
	            System.out.println("Filtered Product: " + product.getName());
	            System.out.println("Quantity: " + product.getQuantity());
	        }
	    } else {
	        System.out.println("Product list is null!");
	        filteredProducts = List.of(); // Empty list if null
	    }

	    model.addAttribute("products", filteredProducts);
	    model.addAttribute("buyerType", buyerType);
	    return "buyerdashboard";
	}

	
	@PostMapping("/buyer/save")
	public String BuyerRegistration(@Valid @ModelAttribute("buyerDto") BuyerDto buyerDto, BindingResult result, Model model) {
	    Buyer existingBuyer = buyerService.findBuyerByEmail(buyerDto.getEmail());
		logger.debug("Received buyerDto: {}", buyerDto);
		logger.debug("Received buyerDto email: {}", buyerDto.getEmail());
		logger.debug("Received buyerDto password: {}", buyerDto.getPassword());
		logger.debug("Received buyerDto confirmPassword: {}", buyerDto.getConfirmPassword());
		logger.debug("Received buyerDto firstName: {}", buyerDto.getFirstName());
		logger.debug("Received buyerDto lastName: {}", buyerDto.getLastName());
		logger.debug("Received buyerDto buyerType: {}", buyerDto.getBuyerType());

	    if (existingBuyer != null) {
			logger.debug("An account with this email already exists");
	        result.rejectValue("email", null, "An account with this email already exists");
	    }
	    if (result.hasErrors()) {
			logger.debug("Validation errors found: {}", result.getAllErrors());
	        model.addAttribute("buyerDto", buyerDto); // Update to buyerDto
	        return "buyer"; // Return to registration page if there are errors
	    }
	    if (!buyerDto.isPasswordConfirmed()) {
			logger.debug("Passwords do not match");
	        result.rejectValue("confirmPassword", null, "Passwords do not match"); // Change to confirmPassword
	        model.addAttribute("buyerDto", buyerDto); // Update to buyerDto
	        return "buyer"; // Return to registration page if passwords don't match
	    }
	    try {
			logger.debug("Saving user...");
	        buyerService.saveBuyer(buyerDto); // Save the user
	    } catch (IOException e) {
	        logger.error("Error saving user: ", e);
	        result.rejectValue("general", null, "Error saving user. Please try again.");
	        model.addAttribute("buyerDto", buyerDto); // Update to buyerDto
	        return "buyer"; // Return to registration page if save fails
	    }
	    return "redirect:/login";
	}



	@GetMapping("/buyerdashboard/search")
	public String searchProducts(
			@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "keyword", required = false) String keyword,
			Model model) {

		if (userDetails == null) {
			throw new IllegalStateException("UserDetails is null. Ensure the user is logged in.");
		}

		// Fetch buyer details based on the logged-in user's email
		String email = userDetails.getUsername();
		Buyer buyer = buyerService.findBuyerByEmail(email);

		if (buyer == null) {
			throw new IllegalStateException("Buyer not found for email: " + email);
		}

		String buyerType = buyer.getBuyerType().name(); // Assuming buyerType is an Enum

		List<Product> allProducts = productServiceImpl.getAllProducts();

		List<Product> filteredProducts;

		if (allProducts != null) {
			// Filter products based on buyer type
			if ("BULK_BUYER".equalsIgnoreCase(buyerType)) {
				filteredProducts = allProducts.stream()
						.filter(product -> product.getQuantity() >= 200)
						.toList();
			} else { // SMALL_BUYER
				filteredProducts = allProducts.stream()
						.filter(product -> product.getQuantity() < 200)
						.toList();
			}

			// If a keyword is provided, further filter the products by name, description, or category
			if (keyword != null && !keyword.isEmpty()) {
				filteredProducts = filteredProducts.stream()
						.filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()) ||
								product.getDescription().toLowerCase().contains(keyword.toLowerCase()) ||
								product.getCategory().toLowerCase().contains(keyword.toLowerCase()))
						.toList();
			}

			// Debugging output
			System.out.println("Buyer Type: " + buyerType);
			for (Product product : filteredProducts) {
				System.out.println("Filtered Product: " + product.getName());
				System.out.println("Quantity: " + product.getQuantity());
			}
		} else {
			System.out.println("Product list is null!");
			filteredProducts = List.of(); // Empty list if null
		}

		model.addAttribute("products", filteredProducts);
		model.addAttribute("buyerType", buyerType);
		model.addAttribute("keyword", keyword != null ? keyword : "");
		return "buyerdashboard";
	}

}
