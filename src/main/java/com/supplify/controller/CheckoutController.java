package com.supplify.controller;

import com.supplify.dto.OrderDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Product;
import com.supplify.repository.BuyerRepository;
import com.supplify.service.Implement.ProductServiceImpl;
import com.supplify.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BuyerRepository buyerRepository;

    @GetMapping("/{productId}")
    public String showCheckoutPage(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);

        // Get current buyer
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Buyer buyer = buyerRepository.findByEmail(username);
        model.addAttribute("buyer", buyer);

        return "checkout"; // This will use your checkout.html template
    }

    @PostMapping("/confirm")
    public String confirmPurchase(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam String shippingAddress,
            RedirectAttributes redirectAttributes) {

        try {
            // Get current buyer
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Buyer buyer = buyerRepository.findByEmail(username);

            if (buyer == null) {
                return "redirect:/login";
            }

            // Create OrderDto from checkout form
            OrderDto orderDto = new OrderDto();
            orderDto.setProductId(productId);
            orderDto.setQuantity(quantity);
            orderDto.setShippingAddress(shippingAddress); // Set shipping address

            // Process the order using your existing order service
            orderService.processOrder(orderDto, buyer);

            redirectAttributes.addFlashAttribute("successMessage", "Order placed successfully!");
            return "redirect:/orders/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to place order: " + e.getMessage());
            return "redirect:/checkout/" + productId;
        }
    }
}