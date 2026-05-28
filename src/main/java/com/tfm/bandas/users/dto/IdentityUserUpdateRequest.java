package com.tfm.bandas.users.dto;

/**
 * Solicitud de actualización de datos de usuario hacia MS Identity.
 */
public record IdentityUserUpdateRequest(
    String email,
    String firstName,
    String lastName
) {}
