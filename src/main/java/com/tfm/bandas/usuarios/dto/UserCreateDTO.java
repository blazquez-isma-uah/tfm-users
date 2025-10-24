package com.tfm.bandas.usuarios.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Set;

// Entrada: creación de usuario
public record UserCreateDTO(
        @NotBlank @Email String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String role,
        String secondLastName,
        LocalDate birthDate,
        LocalDate bandJoinDate,
        LocalDate systemSignupDate,
        @Nullable String phone,
        @Nullable String notes,
        @Nullable String profilePictureUrl,
        Set<Long> instrumentIds
) {}

