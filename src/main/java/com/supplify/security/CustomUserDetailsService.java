package com.supplify.security;

import com.supplify.entity.Buyer;
import com.supplify.entity.Role;
import com.supplify.entity.Seller;
import com.supplify.entity.ServiceProvider;
import com.supplify.exception.GlobalExceptionHandler;
import com.supplify.repository.BuyerRepository;
import com.supplify.repository.SellerRepository;
import com.supplify.repository.ServiceProviderRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private SellerRepository sellerRepository;
    private BuyerRepository buyerRepository;
    private ServiceProviderRepository serviceProviderRepository;
    public CustomUserDetailsService(SellerRepository sellerRepository, BuyerRepository buyerRepository,ServiceProviderRepository serviceProviderRepository) {
        this.sellerRepository = sellerRepository;
        this.buyerRepository = buyerRepository;
        this.serviceProviderRepository=serviceProviderRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Seller seller = sellerRepository.findByEmail(email);
        Buyer buyer = buyerRepository.findByEmail(email);
        ServiceProvider serviceProvider=serviceProviderRepository.findByEmail(email);
        if (seller != null ) {
            if ( seller.isApproved()){
                return new org.springframework.security.core.userdetails.User(seller.getEmail(),
                        seller.getPassword(),
                        mapRolesToAuthorities(seller.getRoles()));
            }
            else {
                throw new UsernameNotFoundException("Seller is not  approved");
            }

        } else if (buyer != null) {
            return new CustomBuyerDetails(buyer, mapRolesToAuthorities(buyer.getRoles()));
       }
        else if(serviceProvider!= null) {
          //  System.out.println("Service Provider Roles: " + serviceProvider.getRoles());

            return new CustomServiceProviderDetail(serviceProvider, mapRolesToAuthorities(serviceProvider.getRoles()));
            //return new CustomServiceProviderDetail(serviceProvider,"Admin");
        }
else {
        
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities1(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

}
