package com.tfm.bandas.usuarios.dto;

public record KeycloakUserDetailsResponse(
    String id,
    String username,
    String email,
    String firstName,
    String lastName
) {}

