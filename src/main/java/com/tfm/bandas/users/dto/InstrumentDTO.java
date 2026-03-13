package com.tfm.bandas.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InstrumentDTO(
        Long id,
        Integer version,

        @NotBlank
        @Size(min = 2, max = 100)
        String instrumentName,

        @NotBlank
        @Size(max = 50)
        String voice
) {}
