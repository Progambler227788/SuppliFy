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

    private BigDecimal calculateTotalAmount(Product product, int quantity) {
        if (product.getDiscountedPrice() != null && product.getDiscountedPrice().compareTo(BigDecimal.ZERO) > 0) {
            return product.getDiscountedPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }


    @Override
    public List<Order> getOrdersBySellers(Seller seller, String status) {
        if (status == null || status.isEmpty()) {
            return orderRepository.findByProductSellerOrderByOrderDateDesc(seller);
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
