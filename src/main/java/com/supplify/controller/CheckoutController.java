package com.supplify.controller;

import com.supplify.entity.Product;
import com.supplify.service.Implement.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/{productId}")
    public String showCheckoutPage(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        return "checkout";
    }

    @PostMapping("/confirm")
    public String confirmPurchase(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam String shippingAddress) {
        // Handle the purchase logic (e.g., update product quantity, create an order, etc.)
        productService.processPurchase(productId, quantity);
        return "redirect:/buyerdashboard?purchaseSuccess=true";
    }
}