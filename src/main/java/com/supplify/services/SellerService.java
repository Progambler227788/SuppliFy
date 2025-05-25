package com.supplify.services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.supplify.dto.SellerDto;
import com.supplify.entity.Seller;

public interface SellerService {
    void saveUser (SellerDto sellerDto, MultipartFile[] images) throws IOException;
    Seller findUserByEmail(String email );

    Seller findByEmail(String email);
    List<SellerDto> findAllUsers();
    Seller getLoggedInUser();
    void updateSellerProfile(SellerDto sellerDto, MultipartFile[] images) throws IOException;
    //shahid c3
    Seller findById(UUID id);
    void approveSeller(UUID id);
    /*void save(Seller seller);*/
    void rejectSeller(UUID id);
    List<Seller> getPendingSellers();
    public List<Seller> getRejectedSellers();
    //shahid c3
    List<Seller> getAllSellers();
}