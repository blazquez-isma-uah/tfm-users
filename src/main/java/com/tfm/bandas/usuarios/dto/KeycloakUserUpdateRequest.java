package com.tfm.bandas.usuarios.dto;

public record KeycloakUserUpdateRequest(
    String username,
    String email,
    String firstName,
    String lastName
) {}
