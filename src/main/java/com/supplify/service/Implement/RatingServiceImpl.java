package com.supplify.service.Implement;

import com.supplify.dto.RatingRequest;
import com.supplify.entity.Order;
import com.supplify.entity.Product;
import com.supplify.entity.Rating;
import com.supplify.repository.OrderRepository;
import com.supplify.repository.ProductRepository;
import com.supplify.repository.RatingRepository;
import com.supplify.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RatingServiceImpl implements RatingService {
    private final RatingRepository ratingRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public RatingServiceImpl(RatingRepository ratingRepository, OrderRepository orderRepository, ProductRepository productRepository) {
        this.ratingRepository = ratingRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }


    @Override
    @Transactional
    public Rating submitRating(Long orderId, UUID buyerId, RatingRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getBuyer().getId().equals(buyerId)) {
            throw new RuntimeException("Order does not belong to this buyer");
        }

        if (!"DELIVERED".equals(order.getStatus())) {
            throw new RuntimeException("Only delivered orders can be rated");
        }

        if (order.isRated()) {
            throw new RuntimeException("This order has already been rated");
        }

        Rating rating = new Rating();
        rating.setProduct(order.getProduct());
        rating.setBuyer(order.getBuyer());
        rating.setOrderId(order.getId());
        rating.setRatingValue(request.getRatingValue());
        rating.setReview(request.getReview());
        rating.setCreatedAt(LocalDateTime.now());

        Rating savedRating = ratingRepository.save(rating);

        order.setRating(savedRating);
        order.setRated(true);
        orderRepository.save(order);

        updateProductRating(order.getProduct().getId());

        return savedRating;
    }
    @Override
    public void updateProductRating(Long productId) {
        Double averageRating = ratingRepository.calculateAverageRatingByProductId(productId);
        Long ratingCount = ratingRepository.countByProductId(productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setAverageRating(averageRating);
        product.setRatingCount(ratingCount);
        productRepository.save(product);
    }
}
