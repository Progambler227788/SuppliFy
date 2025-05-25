package com.supplify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.supplify.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);

//    Optional<Role> findByNameStartsWith(String name);
}