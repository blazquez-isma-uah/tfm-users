package com.tfm.bandas.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.util.Set;

// Salida: datos públicos de usuario
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("version") Integer version,
        @JsonProperty("username") String username,
        @JsonProperty("iamId") String iamId,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("secondLastName") String secondLastName,
        @JsonProperty("email") String email,
        @JsonProperty("birthDate") LocalDate birthDate,
        @JsonProperty("bandJoinDate") LocalDate bandJoinDate,
        @JsonProperty("systemSignupDate") LocalDate systemSignupDate,
        @JsonProperty("phone") String phone,
        @JsonProperty("notes") String notes,
        @JsonProperty("profilePictureUrl") String profilePictureUrl,
        @JsonProperty("active") boolean active,
        @JsonProperty("roles") Set<String> roles,
        @JsonProperty("instruments") Set<InstrumentDTO> instruments
) {}
