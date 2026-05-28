package com.tfm.bandas.users.dto;

public record IdentityUserDetailsResponse(
    String id,
    String username,
    String email,
    String firstName,
    String lastName
) {}

