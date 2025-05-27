package com.supplify.controller;

import com.supplify.dto.SellerDto;
import com.supplify.services.ServiceProviderService;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;


@Controller
@Slf4j
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

            // Check if the user is a Seller or Buyer

            System.out.println("===== LOGIN ATTEMPT =====");
            System.out.println("Username/Email attempted: " + username);

            System.out.println("===== ALL SELLERS =====");
            List<SellerDto> sellers = sellerService.findAllUsers();
            sellers.forEach(seller -> System.out.println(seller.getEmail()));

            Seller seller = sellerService.findSellerByEmail(email);
            Buyer buyer = buyerService.findBuyerByEmail(email);
//            ServiceProviderService sp = serviceProviderService.findServiceProviderByEmail(email);
            if (seller != null) {
                return "redirect:/sellerdashboard";
            }
            else if (buyer != null) {
                return "redirect:/buyerdashboard";
            }
        }
        return "redirect:/login";
    }
}