package com.supplify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.supplify.entity.Seller;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SellerRepository extends JpaRepository<Seller, UUID> {
    Seller findByEmail(String email);

    Optional<Seller> findById(UUID id);
    List<Seller> findByIsApprovedFalse();
    List<Seller> findByIsApprovedFalseAndIsRejectedFalse(); // For pending sellers
    List<Seller> findByIsRejectedTrue();
    @Query("SELECT s FROM Seller s")
    List<Seller> findAllSellers();

    // For rejected sellers
}