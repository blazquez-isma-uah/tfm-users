package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstrumentRequestDTO(
        @NotBlank
        @Size(min = 2, max = 100)
        @JsonProperty("instrumentName")
        String instrumentName,

        @NotBlank
        @Size(max = 50)
        @JsonProperty("voice")
        String voice
) {}
