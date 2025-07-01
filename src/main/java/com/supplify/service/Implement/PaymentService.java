package com.supplify.service.Implement;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.supplify.dto.StripeIntentResponse;
import com.supplify.entity.Payment;
import com.supplify.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey; // Add your key from .env file


    @Autowired
    private PaymentRepository paymentRepository;

    public StripeIntentResponse createPaymentIntent(double amount, String currency, String userId) {
        Stripe.apiKey = stripeApiKey;

        try {
            // Create or retrieve customer
            Customer customer = Customer.create(Map.of(
                    "name", "User_" + userId,
                    "metadata", Map.of("userId", userId)
            ));

            // Create PaymentIntent
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (amount * 100))
                    .setCurrency(currency)
                    .setCustomer(customer.getId())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Create Ephemeral Key
            EphemeralKeyCreateParams ephemeralKeyParams = EphemeralKeyCreateParams.builder()
                    .setCustomer(customer.getId())
                    .setStripeVersion("2023-08-16")
                    .build();

            EphemeralKey ephemeralKey = EphemeralKey.create(ephemeralKeyParams);

            return new StripeIntentResponse(
                    paymentIntent.getClientSecret(),
                    ephemeralKey.getSecret(),
                    customer.getId(),
                    paymentIntent.getId()
            );
        } catch (StripeException e) {
            throw new RuntimeException("Payment failed: " + e.getMessage());
        }
    }

    public PaymentIntent confirmPayment(String paymentIntentId) throws StripeException {
        PaymentIntent intent = PaymentIntent.retrieve(paymentIntentId);
        if (!"succeeded".equals(intent.getStatus())) {
            throw new RuntimeException("Payment not completed");
        }
        return intent;
    }

    public void savePayment(Long orderId, PaymentIntent intent) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setPaymentIntentId(intent.getId());
        payment.setCustomerId(intent.getCustomer());
        payment.setAmount(new BigDecimal(intent.getAmount() / 100.0));
        payment.setCurrency(intent.getCurrency());
        payment.setStatus(intent.getStatus());
        payment.setReceiptUrl(intent.getReceiptEmail());

        paymentRepository.save(payment);
    }
}
