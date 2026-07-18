package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MyProfileUpdateRequestDTO(
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    @JsonProperty("firstName")
    String firstName,

    @Size(max = 100, message = "El primer apellido no puede superar los 100 caracteres.")
    @JsonProperty("lastName")
    String lastName,

    @Size(max = 100, message = "El segundo apellido no puede superar los 100 caracteres.")
    @JsonProperty("secondLastName")
    String secondLastName,

    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres.")
    @JsonProperty("phone")
    String phone,

    @Size(max = 500, message = "Las notas no pueden superar los 500 caracteres.")
    @JsonProperty("notes")
    String notes,

    @Past(message = "La fecha de nacimiento debe ser anterior a hoy.")
    @JsonProperty("birthDate")
    LocalDate birthDate
) {}
