package com.tfm.bandas.usuarios.dto;

import jakarta.validation.constraints.NotBlank;

public record KeycloakRoleRegisterRequest(
    @NotBlank String name,
    String description
) {}

