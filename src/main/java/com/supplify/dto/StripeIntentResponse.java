package com.supplify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StripeIntentResponse {
    private String clientSecret;
    private String ephemeralKey;
    private String customerId;
    private String paymentIntentId;
    private Long orderId;

    public StripeIntentResponse(String clientSecret, String ephemeralKey,
                                String customerId, String paymentIntentId) {
        this.clientSecret = clientSecret;
        this.ephemeralKey = ephemeralKey;
        this.customerId = customerId;
        this.paymentIntentId = paymentIntentId;
    }
}