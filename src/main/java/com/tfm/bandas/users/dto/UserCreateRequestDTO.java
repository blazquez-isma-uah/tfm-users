package com.tfm.bandas.users.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Set;

// Entrada: creación de usuario
public record UserCreateRequestDTO(
        @NotBlank @Email String email,
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String firstName,
        @NotBlank String lastName,
        String secondLastName,
        LocalDate birthDate,
        LocalDate bandJoinDate,
        LocalDate systemSignupDate,
        @Nullable String phone,
        @Nullable String notes,
        @Nullable String profilePictureUrl,
        Set<Long> instrumentIds,
        Set<String> roles
) {
    @Override
    public String toString() {
        return "UserCreateRequestDTO[" +
                "email=" + email() +
                ", username=" + username() +
                ", firstName=" + firstName() +
                ", lastName=" + lastName() +
                ", secondLastName=" + secondLastName() +
                ", birthDate=" + birthDate() +
                ", bandJoinDate=" + bandJoinDate() +
                ", systemSignupDate=" + systemSignupDate() +
                ", phone=" + phone() +
                ", notes=" + notes() +
                ", profilePictureUrl=" + profilePictureUrl() +
                ", instrumentIds=" + instrumentIds() +
                ", roles=" + roles() +
                "]";
    }
}
