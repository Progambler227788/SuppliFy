package com.supplify.repository;


import com.supplify.entity.Buyer;
import com.supplify.entity.Order;
import com.supplify.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByBuyerOrderByOrderDateDesc(Buyer buyer);
    List<Order> findByProductSellerOrderByOrderDateDesc(Seller seller);
    List<Order> findByProductSellerAndStatusOrderByOrderDateDesc(Seller seller, String status);
}