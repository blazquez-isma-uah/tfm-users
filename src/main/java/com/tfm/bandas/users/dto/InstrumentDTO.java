package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InstrumentDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("version") Integer version,

        @NotBlank
        @Size(min = 2, max = 100)
        @JsonProperty("instrumentName")
        String instrumentName,

        @NotBlank
        @Size(max = 50)
        @JsonProperty("voice")
        String voice
) {}
