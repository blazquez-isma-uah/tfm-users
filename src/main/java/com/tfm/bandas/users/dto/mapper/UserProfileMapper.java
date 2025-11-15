package com.tfm.bandas.users.dto.mapper;

import com.tfm.bandas.users.dto.KeycloakUserRegisterRequest;
import com.tfm.bandas.users.dto.UserCreateRequestDTO;
import com.tfm.bandas.users.dto.UserDTO;
import com.tfm.bandas.users.model.entity.InstrumentEntity;
import com.tfm.bandas.users.model.entity.UserProfileEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class UserProfileMapper {

    private UserProfileMapper() {
        // Constructor privado para evitar instanciación
    }

    // Convierte UserProfileEntity a UserResponseDTO
    public static UserDTO toDTO(UserProfileEntity userProfile) {
        if (userProfile == null) {
            return null;
        }
        return new UserDTO(
                userProfile.getId(),
                userProfile.getVersion(),
                userProfile.getUsername(),
                userProfile.getIamId(),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                userProfile.getSecondLastName(),
                userProfile.getEmail(),
                userProfile.getBirthDate(),
                userProfile.getBandJoinDate(),
                userProfile.getSystemSignupDate(),
                userProfile.getPhone(),
                userProfile.getNotes(),
                userProfile.getProfilePictureUrl(),
                userProfile.isActive(),
                mapRoleNamesToSet(userProfile.getRoleNames()),
                mapInstruments(userProfile.getInstruments())
        );
    }

    // Convierte UserCreateDTO a UserProfileEntity
    public static UserProfileEntity toEntityFromCreateDTO(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO == null) {
            return null;
        }
        return UserProfileEntity.builder()
                .username(userCreateRequestDTO.username())
                .firstName(userCreateRequestDTO.firstName())
                .lastName(userCreateRequestDTO.lastName())
                .secondLastName(userCreateRequestDTO.secondLastName())
                .email(userCreateRequestDTO.email())
                .birthDate(userCreateRequestDTO.birthDate())
                .bandJoinDate(userCreateRequestDTO.bandJoinDate())
                .systemSignupDate(userCreateRequestDTO.systemSignupDate())
                .phone(userCreateRequestDTO.phone())
                .notes(userCreateRequestDTO.notes())
                .profilePictureUrl(userCreateRequestDTO.profilePictureUrl())
                .build();
    }

    // Convierte UserCreateDTO a KeycloakUserRegisterRequest
    public static KeycloakUserRegisterRequest toKeycloakUserRegisterRequest(UserCreateRequestDTO userCreateRequestDTO) {
        if (userCreateRequestDTO == null) {
            return null;
        }
        String lastName = userCreateRequestDTO.lastName();
        if (userCreateRequestDTO.secondLastName() != null && !userCreateRequestDTO.secondLastName().isEmpty()) {
            lastName = lastName + " " + userCreateRequestDTO.secondLastName();
        }
        return new KeycloakUserRegisterRequest(
                userCreateRequestDTO.username(),
                userCreateRequestDTO.email(),
                userCreateRequestDTO.password(),
                userCreateRequestDTO.roles(),
                userCreateRequestDTO.firstName(),
                lastName
        );
    }

    // Convierte Set<InstrumentEntity> a Set<String> (instrumentName + " " + voice)
    private static Set<String> mapInstruments(Set<InstrumentEntity> instruments) {
        if (instruments == null || instruments.isEmpty()) {
            return Collections.emptySet();
        }
        return instruments.stream()
                .map(i -> i.getInstrumentName() + " " + i.getVoice())
                .collect(Collectors.toSet());
    }

    // Convierte "ROLE_A,ROLE_B" -> Set<"ROLE_A","ROLE_B">
    private static Set<String> mapRoleNamesToSet(String roleNames) {
        if (roleNames == null || roleNames.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(roleNames.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    // Convierte Set<"ROLE_A","ROLE_B"> -> "ROLE_A,ROLE_B"
    public static String mapSetToRoleNames(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }
}
