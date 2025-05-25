package com.supplify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
	
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<Seller> sellers = new ArrayList<>();
    
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<Buyer> buyers = new ArrayList<>();
    
    @ManyToMany(mappedBy = "roles")
    private List<ServiceProvider> serviceProviders  = new ArrayList<>();
}