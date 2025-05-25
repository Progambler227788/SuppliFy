package com.supplify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.supplify.entity.Buyer;

import java.util.UUID;

public interface BuyerRepository extends JpaRepository<Buyer, UUID> {
    Buyer findByEmail(String email);
}