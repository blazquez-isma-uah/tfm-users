package com.tfm.bandas.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

/**
 * Solicitud de creación de usuario hacia MS Identity.
 * El campo roles no es consumido por Identity (la asignación de rol se hace
 * en una llamada separada), pero MS Users lo usa internamente para orquestar
 * la asignación tras recibir el iamId del usuario creado.
 */
public record IdentityUserRegisterRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password,
        Set<String> roles,
        String firstName,
        String lastName
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
