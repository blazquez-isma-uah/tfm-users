package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserUpdateRequestDTO(
        @JsonProperty("email") @NotBlank(message = "El email es obligatorio.") @Email(message = "El email no tiene un formato válido.") String email,
        @JsonProperty("firstName") @NotBlank(message = "El nombre es obligatorio.") String firstName,
        @JsonProperty("lastName") @NotBlank(message = "El primer apellido es obligatorio.") String lastName,
        @JsonProperty("secondLastName") String secondLastName,
        @JsonProperty("birthDate") LocalDate birthDate,
        @JsonProperty("bandJoinDate") LocalDate bandJoinDate,
        @JsonProperty("phone") @Nullable String phone,
        @JsonProperty("notes") @Nullable String notes

) {}