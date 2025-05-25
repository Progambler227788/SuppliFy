package com.supplify.services;

import com.supplify.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    UUID findIdByEmail(String email);
    String getRoleByEmail(String email);
    Optional<?> getUserById(UUID userId);

    Optional<User> getUserByEmail(String email);
}

