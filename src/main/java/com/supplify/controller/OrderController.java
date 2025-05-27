package com.supplify.controller;

import com.supplify.dto.OrderDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Order;
import com.supplify.entity.Product;
import com.supplify.repository.BuyerRepository;
import com.supplify.repository.OrderRepository;
import com.supplify.repository.ProductRepository;
import com.supplify.services.OrderService;
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
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private OrderRepository orderRepository;




    /**
     * View order history for the buyer
     */
    @GetMapping("/history")
    public String viewOrderHistory(Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve buyer
            Buyer buyer = buyerRepository.findByEmail(username);
            if (buyer == null) {
                logger.error("No buyer found for username: {}", username);
                return "redirect:/login";
            }

            // Retrieve orders
            List<Order> orders = orderRepository.findByBuyerOrderByOrderDateDesc(buyer);

            model.addAttribute("orders", orders);
            model.addAttribute("buyer", buyer);

            return "order_history";
        } catch (Exception e) {
            logger.error("Error retrieving order history", e);
            return "redirect:/error";
        }
    }

    /**
     * View order details
     */
    @GetMapping("/details/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Retrieve buyer
            Buyer buyer = buyerRepository.findByEmail(username);
            if (buyer == null) {
                logger.error("No buyer found for username: {}", username);
                return "redirect:/login";
            }

            // Retrieve order
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify order belongs to the buyer
            if (!order.getBuyer().getId().equals(buyer.getId())) {
                logger.warn("Unauthorized access to order: {} by buyer: {}",
                        orderId, username);
                return "redirect:/orders/history";
            }

            model.addAttribute("order", order);
            model.addAttribute("buyer", buyer);

            return "order_details";
        } catch (Exception e) {
            logger.error("Error retrieving order details", e);
            return "redirect:/error";
        }
    }
}