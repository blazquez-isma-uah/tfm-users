package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Respuesta básica del proveedor de identidad tras operaciones de usuario.
 * id = sub UUID asignado por el proveedor (Cognito o Keycloak).
 * Se almacena como iam_id en user_profile para identificar al usuario entre servicios.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityUserResponse(
        @JsonProperty("id") String id,
        @JsonProperty("username") String username
) {}
