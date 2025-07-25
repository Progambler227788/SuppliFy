package com.supplify.repository;

import com.supplify.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByProductId(Long productId);
    Optional<Rating> findByOrderIdAndBuyerId(Long orderId, UUID buyerId);

    Long countByProductId(Long productId);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.product.id = :productId")
    Double calculateAverageRatingByProductId(@Param("productId") Long productId);

}
