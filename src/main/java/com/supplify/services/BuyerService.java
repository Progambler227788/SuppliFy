package com.supplify.services;

import java.io.IOException;
import java.util.List;

import com.supplify.dto.BuyerDto;
import com.supplify.entity.Buyer;

public interface BuyerService {
    void saveBuyer(BuyerDto buyerDto) throws IOException;
    Buyer findBuyerByEmail(String email);
    List<BuyerDto> findAllBuyers();
}