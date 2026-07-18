package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Validación mínima alineada con la política de Amazon Cognito:
 * mínimo 8 caracteres, mayúscula, minúscula y dígito.
 * La validación de complejidad completa la realiza Cognito en el proveedor.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PasswordUpdateRequestDTO(
        @NotBlank(message = "La nueva contraseña es obligatoria.")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
        @JsonProperty("newPassword")
        String newPassword
) {
    @Override
    public String toString() {
        return "PasswordUpdateRequestDTO[newPassword=****]";
    }
}