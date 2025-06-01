package com.supplify.controller;

import com.stripe.model.PaymentIntent;
import com.supplify.dto.OrderDto;
import com.supplify.dto.StripeIntentResponse;
import com.supplify.entity.Buyer;
import com.supplify.entity.Product;
import com.supplify.repository.BuyerRepository;
import com.supplify.service.Implement.PaymentService;
import com.supplify.service.Implement.ProductServiceImpl;
import com.supplify.services.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    @Autowired
    private ProductServiceImpl productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private PaymentService paymentService;

    private final String stripePublishableKey = "";

    @GetMapping("/{productId}")
    public String showCheckoutPage(@PathVariable Long productId, Model model, Authentication authentication) {
        Product product = productService.getProductById(productId);
        model.addAttribute("product", product);
        model.addAttribute("buyer", buyerRepository.findByEmail(authentication.getName()));
        model.addAttribute("stripePublishableKey", stripePublishableKey);
        return "checkout";
    }

    @PostMapping("/create-payment-intent")
    @ResponseBody
    public ResponseEntity<?> createPaymentIntent(
            @RequestParam Long productId,
            @RequestParam int quantity,
            @RequestParam String shippingAddress,
            Authentication authentication) {

        try {
            Buyer buyer = buyerRepository.findByEmail(authentication.getName());
            if (buyer == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            Product product = productService.getProductById(productId);
            BigDecimal amount = calculateAmount(product, quantity);

            StripeIntentResponse response = paymentService.createPaymentIntent(
                    amount.doubleValue(), "usd", buyer.getId().toString());

            OrderDto orderDto = new OrderDto();
            orderDto.setProductId(productId);
            orderDto.setQuantity(quantity);
            orderDto.setShippingAddress(shippingAddress);
            orderDto.setPaymentIntentId(response.getPaymentIntentId());

            response.setOrderId(orderService.createPendingOrder(orderDto, buyer).getId());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Payment intent creation failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/confirm-payment")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(
            @RequestParam String paymentIntentId,
            @RequestParam Long orderId) {

        try {
            PaymentIntent intent = paymentService.confirmPayment(paymentIntentId);

            orderService.completeOrderAfterPayment(orderId, paymentIntentId);

            paymentService.savePayment(orderId, intent);

            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            log.error("Payment confirmation failed", e);
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    private BigDecimal calculateAmount(Product product, int quantity) {
        BigDecimal price = product.getDiscountedPrice() != null ?
                product.getDiscountedPrice() : product.getPrice();
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}