package com.supplify.services;

import com.supplify.dto.RatingRequest;
import com.supplify.entity.Rating;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public interface RatingService {


    public Rating submitRating(Long orderId, UUID buyerId, RatingRequest request);
    public void updateProductRating(Long productId);

}
