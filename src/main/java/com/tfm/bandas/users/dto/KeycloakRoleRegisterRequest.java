package com.tfm.bandas.users.dto;

import jakarta.validation.constraints.NotBlank;

public record KeycloakRoleRegisterRequest(
    @NotBlank String name,
    String description
) {}

