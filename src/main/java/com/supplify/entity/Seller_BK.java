package com.supplify.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name="Sellers")
public class Seller_BK extends User implements Serializable {

	public Seller_BK() {
		this.setRole("SELLER");
	}
    private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(generator = "UUID") // Automatically generates UUID
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
	private UUID id;

    @Column(nullable=false)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "SellerDocument_images", joinColumns = @JoinColumn(name = "seller_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();
    
    @Column(nullable=false)
	private String shopname;
    
    
    @Column(nullable=false)
	private String address;
    
   
    @Column(nullable=false)
	private String zipcode;

    
	
	
	  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	  @JoinTable(
			    name = "seller_roles",
			    joinColumns = @JoinColumn(name = "seller_id"),
			    inverseJoinColumns = @JoinColumn(name = "role_id")
			)
			private Set<Role> roles = new HashSet<>();
	// Shahid c2
	private boolean approved = false;
	// Shahid c2
	 }
