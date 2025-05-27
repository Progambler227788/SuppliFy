package com.supplify.service.Implement;

import com.supplify.dto.OrderDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Order;
import com.supplify.entity.Product;
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
}
