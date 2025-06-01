package com.supplify.controller;

import com.supplify.dto.RatingRequest;
import com.supplify.entity.Order;
import com.supplify.entity.Rating;
import com.supplify.repository.OrderRepository;
import com.supplify.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    private final OrderRepository orderRepository;

    @Autowired
    public RatingController(RatingService ratingService, OrderRepository orderRepository) {
        this.ratingService = ratingService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/submit")
    public String submitRating(@RequestParam Long orderId,
                               @RequestParam UUID buyerId,
                               @RequestParam int ratingValue,
                               @RequestParam(required = false) String review,
                               Model model) {
        RatingRequest request = new RatingRequest();
        request.setRatingValue(ratingValue);
        request.setReview(review);

        Order order = null; // Initialize order to null

        try {
            Rating rating = ratingService.submitRating(orderId, buyerId, request);
            model.addAttribute("success", "Thank you for your feedback!");
            order =  orderRepository.findById(orderId).orElse(null); // Retrieve order from the database

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            // Fetch the order even if there's an error
            order = orderRepository.findById(orderId)
                    .orElse(null); // Handle the case where the order might not exist
        }

        // Always add the order to the model
        if (order != null) {
            model.addAttribute("order", order);
        } else {
            model.addAttribute("error", "Order not found.");
        }

        return "order_details"; // Return to order details page
    }
}