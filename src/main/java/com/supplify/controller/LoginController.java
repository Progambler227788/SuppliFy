package com.supplify.controller;

import com.supplify.services.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.supplify.entity.Buyer;
import com.supplify.entity.Seller;
import com.supplify.services.BuyerService;
import com.supplify.services.SellerService;

import jakarta.servlet.http.HttpSession;



@Controller
public class LoginController {

	@Autowired
	private SellerService sellerService;
	@Autowired
	private SellerService userService;
	@Autowired
	private BuyerService buyerService;
    @Autowired
    private ServiceProviderService serviceProviderService;

    @GetMapping("/login")
	public String loginPage() {
	    return "login";
	}

	
    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("password") String password, Model model) {
        // Authenticate the user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            Seller seller = sellerService.findByEmail(email);
            Buyer buyer = buyerService.findBuyerByEmail(email);
//            ServiceProviderService sp = serviceProviderService.findServiceProviderByEmail(email);
            if (seller != null) {
                return "redirect:/sellerdashboard";
            } else if (buyer != null) {
                return "redirect:/buyerdashboard";
            }
        }
        return "redirect:/login";
    }
}