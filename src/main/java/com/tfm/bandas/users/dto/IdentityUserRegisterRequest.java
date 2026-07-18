package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * Solicitud de creación de usuario hacia MS Identity.
 * El campo roles no es consumido por Identity (la asignación de rol se hace
 * en una llamada separada), pero MS Users lo usa internamente para orquestar
 * la asignación tras recibir el iamId del usuario creado.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record IdentityUserRegisterRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio.") @JsonProperty("username") String username,
        @JsonProperty("email") @Email(message = "El email no tiene un formato válido.") String email,
        @NotBlank(message = "La contraseña es obligatoria.") @JsonProperty("password") String password,
        @JsonProperty("roles") Set<String> roles,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName
) {
    @Override
    public String toString() {
        return "IdentityUserRegisterRequest[" +
                "username=" + username() +
                ", email=" + email() +
                ", roles=" + roles() +
                ", firstName=" + firstName() +
                ", lastName=" + lastName() +
                "]";
    }
}
