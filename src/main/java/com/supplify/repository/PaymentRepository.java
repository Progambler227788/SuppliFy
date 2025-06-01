package com.supplify.repository;


import com.supplify.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Payment findByPaymentIntentId(String paymentIntentId);
    Payment findByOrderId(Long orderId);
}
