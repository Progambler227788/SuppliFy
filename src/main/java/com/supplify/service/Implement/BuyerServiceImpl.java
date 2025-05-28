package com.supplify.service.Implement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.supplify.dto.BuyerDto;
import com.supplify.entity.Buyer;
import com.supplify.entity.Role;
import com.supplify.entity.Seller;
import com.supplify.repository.BuyerRepository;
import com.supplify.repository.RoleRepository;
import com.supplify.services.BuyerService;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BuyerServiceImpl implements BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public BuyerServiceImpl(BuyerRepository buyerRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.buyerRepository = buyerRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void saveBuyer(BuyerDto buyerDto) {
        Buyer buyer = new Buyer();
        System.out.println("Inside saveBuyer method");

        System.out.println("" + buyerDto.getFirstName() + " " + buyerDto.getLastName() + " " + buyerDto.getEmail() + " " + buyerDto.getPhone() + " " + buyerDto.getBuyerType() + " " + buyerDto.getPassword());
        buyer.setFirstName(buyerDto.getFirstName());
        buyer.setLastName(buyerDto.getLastName());
        buyer.setEmail(buyerDto.getEmail());
        buyer.setPhone(buyerDto.getPhone());
        buyer.setBuyerType(buyerDto.getBuyerType());
        buyer.setPassword(passwordEncoder.encode(buyerDto.getPassword()));

        Role role = roleRepository.findByName("BUYER");
        if (role == null) {
            role = new Role();
            role.setName("BUYER");
            role = roleRepository.save(role);
        }

        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        buyer.setRoles(roleSet);
        buyerRepository.save(buyer);
    }

    @Override
    public Buyer findBuyerByEmail(String email) {
        return buyerRepository.findByEmail(email);
    }
    @Override
    public void updateBuyer(Buyer buyer, BuyerDto buyerDto) throws IOException {
        // Update basic info
        buyer.setFirstName(buyerDto.getFirstName());
        buyer.setLastName(buyerDto.getLastName());
        buyer.setPhone(buyerDto.getPhone());
        buyer.setEmail(buyerDto.getEmail());
        buyer.setBuyerType(buyerDto.getBuyerType());

        // Update password if provided
        if (StringUtils.hasText(buyerDto.getPassword())) {
            buyer.setPassword(passwordEncoder.encode(buyerDto.getPassword()));
        }

        buyerRepository.save(buyer);
    }

    @Override
    public List<BuyerDto> findAllBuyers() {
        List<Buyer> buyers = buyerRepository.findAll();
        return buyers.stream()
                .map((buyer) -> mapToBuyerDto(buyer))
                .collect(Collectors.toList());
    }

    private BuyerDto mapToBuyerDto(Buyer buyer) {
        BuyerDto buyerDto = new BuyerDto();
        String[] str = buyer.getFirstName().split(" ");
        buyerDto.setFirstName(str[0]);
        buyerDto.setLastName(str[1]);
        buyerDto.setEmail(buyer.getEmail());
        return buyerDto;
    }
}