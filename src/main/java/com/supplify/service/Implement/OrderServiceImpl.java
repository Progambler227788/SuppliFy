package com.supplify.service.Implement;

import com.supplify.dto.OrderDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Order;
import com.supplify.entity.Product;
import com.supplify.entity.Seller;
import com.supplify.repository.OrderRepository;
import com.supplify.repository.ProductRepository;
import com.supplify.services.OrderService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    @Transactional
    public void processOrder(OrderDto orderDto, Buyer buyer) {
        try {
            Product product = productRepository.findById(orderDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getQuantity() < orderDto.getQuantity()) {
                throw new RuntimeException("Not enough stock available");
            }

            BigDecimal totalAmount = calculateTotalAmount(product, orderDto.getQuantity());

            Order order = new Order();
            order.setBuyer(buyer);
            order.setProduct(product);
            order.setQuantity(orderDto.getQuantity());
            order.setTotalAmount(totalAmount);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING");
            order.setShippingAddress(orderDto.getShippingAddress()); // Set shipping address

            orderRepository.save(order);

            product.setQuantity(product.getQuantity() - orderDto.getQuantity());
            productRepository.save(product);
        } catch (Exception e) {
            logger.error("Error processing order", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void completeOrderAfterPayment(Long orderId, String paymentIntentId) {

        try {

            logger.info("Order Service Implementation", "Inside the complete order");

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            // Verify payment intent matches
            if (!paymentIntentId.equals(order.getPaymentIntentId())) {
                throw new RuntimeException("Payment intent mismatch");
            }

            // Update order status
            order.setStatus("PENDING");
            orderRepository.save(order);

            // Now deduct inventory
            Product product = order.getProduct();
            product.setQuantity(product.getQuantity() - order.getQuantity());
            productRepository.save(product);

        }

        catch (Exception e) {
            logger.error("Error processing order", e);
            throw e;
        }
    }

    @Override
    public Order findByPaymentIntentId(String paymentIntentId) {
        return orderRepository.findByPaymentIntentId(paymentIntentId)
                .orElseThrow(() -> new RuntimeException("Order not found for payment intent"));
    }

    @Override
    @Transactional
    public Order createPendingOrder(OrderDto orderDto, Buyer buyer) {
        try {
            Product product = productRepository.findById(orderDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Calculate total amount but DON'T deduct inventory yet
            BigDecimal totalAmount = calculateTotalAmount(product, orderDto.getQuantity());

            Order order = new Order();
            order.setBuyer(buyer);
            order.setProduct(product);
            order.setQuantity(orderDto.getQuantity());
            order.setTotalAmount(totalAmount);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus("PENDING_PAYMENT"); // Different status for pending payment
            order.setShippingAddress(orderDto.getShippingAddress());
            order.setPaymentIntentId(orderDto.getPaymentIntentId()); // Store payment intent ID

            return orderRepository.save(order);
        } catch (Exception e) {
            logger.error("Error creating pending order", e);
            throw e;
        }
    }


    private BigDecimal calculateTotalAmount(Product product, int quantity) {
        if (product.getDiscountedPrice() != null && product.getDiscountedPrice().compareTo(BigDecimal.ZERO) > 0) {
            return product.getDiscountedPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }


    @Override
    public List<Order> getOrdersBySellers(Seller seller, String status) {
        if (status == null || status.isEmpty()) {
            List<Order> fetchedOrders = orderRepository.findByProductSellerOrderByOrderDateDesc(seller);
            return fetchedOrders.stream()
                    .filter(order -> !order.getStatus().equalsIgnoreCase("PENDING_PAYMENT"))
                    .toList();
        }

        return orderRepository.findByProductSellerAndStatusOrderByOrderDateDesc(seller, status);
    }


    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setStatus(status);
        orderRepository.save(order);
    }

}
