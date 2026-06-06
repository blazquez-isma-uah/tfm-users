package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MyProfileUpdateRequestDTO(
    @Size(max = 100, message = "First name must not exceed 100 characters")
    @JsonProperty("firstName")
    String firstName,

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @JsonProperty("lastName")
    String lastName,

    @Size(max = 100, message = "Second last name must not exceed 100 characters")
    @JsonProperty("secondLastName")
    String secondLastName,

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @JsonProperty("phone")
    String phone,

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @JsonProperty("notes")
    String notes,

    @Size(max = 255, message = "Profile picture URL must not exceed 255 characters")
    @JsonProperty("profilePictureUrl")
    String profilePictureUrl,

    @Past(message = "Birth date must be in the past")
    @JsonProperty("birthDate")
    LocalDate birthDate
) {}
