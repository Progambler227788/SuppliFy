package com.supplify.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.supplify.dto.ServiceProviderDto;
import com.supplify.entity.ServiceProvider;
import com.supplify.services.ServiceProviderService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ServiceProviderController {
	
	private static final Logger logger = LoggerFactory.getLogger(ServiceProviderController.class);
	@Autowired
	private final ServiceProviderService serviceProviderService;
	
	public ServiceProviderController(ServiceProviderService serviceProviderService) {
		this.serviceProviderService=serviceProviderService;
	}
	
	@GetMapping("/ServiceProvider")
	public String buyerRegis(Model model) {
	    model.addAttribute("service", new ServiceProviderDto()); // Change to buyerDto
	    return "ServiceProvider"; // Ensure this returns the correct Thymeleaf template
	}
	
	
	@GetMapping("/SPdashboard")
	public String buyerDashboard(Model model) {
       return "SPdashboard";
	}
	
	@PostMapping("/serviceProvider/save")
	public String BuyerRegistration(@Valid @ModelAttribute("service") ServiceProviderDto serviceProviderDto, BindingResult result, Model model) {
	    ServiceProvider existingSP = serviceProviderService.findServiceProviderByEmail(serviceProviderDto.getEmail());
	    if (existingSP != null) {
	        result.rejectValue("email", null, "An account with this email already exists");
	    }
	    if (result.hasErrors()) {
	        model.addAttribute("service", serviceProviderDto); // Update to buyerDto
	        return "ServiceProvider"; // Return to registration page if there are errors
	    }
	    if (!serviceProviderDto.isPasswordConfirmed()) {
	        result.rejectValue("confirmPassword", null, "Passwords do not match"); // Change to confirmPassword
	        model.addAttribute("service", serviceProviderDto); // Update to buyerDto
	        return "ServiceProvider"; // Return to registration page if passwords don't match
	    }
	    try {
	        serviceProviderService.saveServiceProvider(serviceProviderDto); // Save the user
	    } catch (IOException e) {
	        logger.error("Error saving user: ", e);
	        result.rejectValue("general", null, "Error saving user. Please try again.");
	        model.addAttribute("service", serviceProviderDto); // Update to buyerDto
	        return "ServiceProvider"; // Return to registration page if save fails
	    }
	    return "redirect:/login";
	}
	
	
}
