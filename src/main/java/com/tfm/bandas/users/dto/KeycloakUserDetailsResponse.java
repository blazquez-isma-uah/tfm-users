package com.tfm.bandas.users.dto;

public record KeycloakUserDetailsResponse(
    String id,
    String username,
    String email,
    String firstName,
    String lastName
) {}

