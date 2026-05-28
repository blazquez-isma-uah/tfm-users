package com.tfm.bandas.users.dto;

/**
 * Respuesta básica del proveedor de identidad tras operaciones de usuario.
 * id = sub UUID asignado por el proveedor (Cognito o Keycloak).
 * Se almacena como iam_id en user_profile para identificar al usuario entre servicios.
 */
public record IdentityUserResponse(
        String id,
        String username
) {}
