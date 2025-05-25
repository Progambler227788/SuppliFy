package com.supplify.service.Implement;

import com.supplify.entity.Buyer;
import com.supplify.entity.Seller;
import com.supplify.entity.User;
import com.supplify.repository.BuyerRepository;
import com.supplify.repository.SellerRepository;
import com.supplify.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public UUID findIdByEmail(String email) {
        // Check Buyer repository
        Buyer buyer = buyerRepository.findByEmail(email);
        if (buyer != null) {
            return buyer.getId();
        }

        // Check Seller repository
        Seller seller = sellerRepository.findByEmail(email);
        if (seller != null) {
            return seller.getId();
        }

        throw new IllegalArgumentException("User not found with email: " + email);
    }

    @Override
    public String getRoleByEmail(String email) {
        if (buyerRepository.findByEmail(email) != null) {
            return "BUYER";
        } else if (sellerRepository.findByEmail(email) != null) {
            return "SELLER";
        }

        throw new IllegalArgumentException("User role not found with email: " + email);
    }
    @Override
    public Optional<?> getUserById(UUID userId) {
        // Check if the user exists as a Buyer
        Optional<Buyer> buyer = buyerRepository.findById(userId);
        if (buyer.isPresent()) {
            return buyer; // Return the Buyer if found
        }

        // Check if the user exists as a Seller
        Optional<Seller> seller = sellerRepository.findById(userId);
        if (seller.isPresent()) {
            return seller; // Return the Seller if found
        }

        // If neither Buyer nor Seller is found, return an empty Optional
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        // Check if the user exists as a Buyer
        Buyer buyer = buyerRepository.findByEmail(email);
        if (buyer != null) {
            return Optional.of(buyer);
        }

        // Check if the user exists as a Seller
        Seller seller = sellerRepository.findByEmail(email);
        if (seller != null) {
            return Optional.of(seller);
        }

        // If neither Buyer nor Seller is found, return an empty Optional
        return Optional.empty();
    }


    public Optional<Seller> getSellerById(UUID sellerId) {
        return sellerRepository.findById(sellerId);
    }


}
