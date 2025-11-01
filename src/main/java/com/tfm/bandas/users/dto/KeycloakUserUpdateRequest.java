package com.tfm.bandas.users.dto;

public record KeycloakUserUpdateRequest(
    String username,
    String email,
    String firstName,
    String lastName
) {}
