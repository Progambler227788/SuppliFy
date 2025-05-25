package com.supplify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.supplify.entity.Product;
import com.supplify.entity.Seller;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findBySeller(Seller seller); // Find products by seller
    
    @Query("SELECT p FROM Product p WHERE p.seller = :seller AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchProductsBySellerAndKeyword(@Param("seller") Seller seller, @Param("keyword") String keyword); // Search products by seller and keyword
     
}

