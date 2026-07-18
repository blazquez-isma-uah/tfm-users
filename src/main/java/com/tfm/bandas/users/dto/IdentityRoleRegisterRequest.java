package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityRoleRegisterRequest(
    @NotBlank(message = "El nombre del rol es obligatorio.") @JsonProperty("name") String name,
    @JsonProperty("description") String description
) {}
