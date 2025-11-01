package com.tfm.bandas.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record KeycloakUserRegisterRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password,
        Set<String> roles,
        String firstName,
        String lastName
) {}
