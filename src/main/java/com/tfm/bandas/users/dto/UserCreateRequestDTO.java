package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Set;

// Entrada: creación de usuario
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserCreateRequestDTO(
        @JsonProperty("email") @NotBlank @Email String email,
        @JsonProperty("username") @NotBlank String username,
        @JsonProperty("password") @NotBlank String password,
        @JsonProperty("firstName") @NotBlank String firstName,
        @JsonProperty("lastName") @NotBlank String lastName,
        @JsonProperty("secondLastName") String secondLastName,
        @JsonProperty("birthDate") LocalDate birthDate,
        @JsonProperty("bandJoinDate") LocalDate bandJoinDate,
        @JsonProperty("systemSignupDate") LocalDate systemSignupDate,
        @JsonProperty("phone") @Nullable String phone,
        @JsonProperty("notes") @Nullable String notes,
        @JsonProperty("instrumentIds") Set<Long> instrumentIds,
        @JsonProperty("roles") Set<String> roles
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
                ", instrumentIds=" + instrumentIds() +
                ", roles=" + roles() +
                "]";
    }
}
