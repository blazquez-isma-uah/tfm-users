package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Solicitud de actualización de datos de usuario hacia MS Identity.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityUserUpdateRequest(
    @JsonProperty("email") String email,
    @JsonProperty("firstName") String firstName,
    @JsonProperty("lastName") String lastName
) {}
