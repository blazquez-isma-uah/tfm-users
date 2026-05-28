package com.tfm.bandas.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Validación mínima alineada con la política de Amazon Cognito:
 * mínimo 8 caracteres, mayúscula, minúscula y dígito.
 * La validación de complejidad completa la realiza Cognito en el proveedor.
 */
public record PasswordUpdateRequestDTO(
        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String newPassword
) {
    @Override
    public String toString() {
        return "PasswordUpdateRequestDTO[newPassword=****]";
    }
}