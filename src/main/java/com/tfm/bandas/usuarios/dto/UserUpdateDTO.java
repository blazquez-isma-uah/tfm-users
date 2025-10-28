package com.tfm.bandas.usuarios.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UserUpdateDTO(
        @NotBlank @Email String email,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String secondLastName,
        LocalDate birthDate,
        LocalDate bandJoinDate,
        @Nullable String phone,
        @Nullable String notes,
        @Nullable String profilePictureUrl
//        Set<Long> instrumentIds,
//        Set<String> roles
) {}