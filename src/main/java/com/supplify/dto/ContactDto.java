package com.supplify.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactDto {
    private UUID id;
    private String name; // Full name of Buyer or Seller
    private String role; // "BUYER" or "SELLER"
}
