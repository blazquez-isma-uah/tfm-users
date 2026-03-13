package com.tfm.bandas.users.dto;

import java.time.LocalDate;
import java.util.Set;

// Salida: datos públicos de usuario
public record UserDTO(
        Long id,
        Integer version,
        String username,
        String iamId,
        String firstName,
        String lastName,
        String secondLastName,
        String email,
        LocalDate birthDate,
        LocalDate bandJoinDate,
        LocalDate systemSignupDate,
        String phone,
        String notes,
        String profilePictureUrl,
        boolean active,
        Set<String> roles,
        Set<InstrumentDTO> instruments
) {}
