package com.supplify.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.supplify.entity.ServiceProvider;

public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {
    ServiceProvider findByEmail(String email);


}
