package com.tfm.bandas.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record KeycloakUserRegisterRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password,
        @NotBlank String role,      // MUSICIAN / ADMIN
        String firstName,
        String lastName
) {}
