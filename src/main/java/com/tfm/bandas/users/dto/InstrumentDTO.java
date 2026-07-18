package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstrumentDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("version") Integer version,

        @NotBlank(message = "El nombre del instrumento es obligatorio.")
        @Size(min = 2, max = 100, message = "El nombre del instrumento debe tener entre 2 y 100 caracteres.")
        @JsonProperty("instrumentName")
        String instrumentName,

        @NotBlank(message = "La voz es obligatoria.")
        @Size(max = 50, message = "La voz no puede superar los 50 caracteres.")
        @JsonProperty("voice")
        String voice
) {}
