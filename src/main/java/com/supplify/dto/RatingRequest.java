package com.supplify.dto;


import jakarta.validation.constraints.*;

public class RatingRequest {
    @Min(1)
    @Max(5)
    private int ratingValue;

    @Size(max = 500)
    private String review;

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
