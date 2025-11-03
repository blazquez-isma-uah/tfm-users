package com.tfm.bandas.users.dto.mapper;

import com.tfm.bandas.users.dto.KeycloakUserRegisterRequest;
import com.tfm.bandas.users.dto.UserCreateDTO;
import com.tfm.bandas.users.dto.UserResponseDTO;
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
    public static UserResponseDTO toDTO(UserProfileEntity userProfile) {
        if (userProfile == null) {
            return null;
        }
        return new UserResponseDTO(
                userProfile.getId(),
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
    public static UserProfileEntity toEntityFromCreateDTO(UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null) {
            return null;
        }
        return UserProfileEntity.builder()
                .username(userCreateDTO.username())
                .firstName(userCreateDTO.firstName())
                .lastName(userCreateDTO.lastName())
                .secondLastName(userCreateDTO.secondLastName())
                .email(userCreateDTO.email())
                .birthDate(userCreateDTO.birthDate())
                .bandJoinDate(userCreateDTO.bandJoinDate())
                .systemSignupDate(userCreateDTO.systemSignupDate())
                .phone(userCreateDTO.phone())
                .notes(userCreateDTO.notes())
                .profilePictureUrl(userCreateDTO.profilePictureUrl())
                .build();
    }

    // Convierte UserCreateDTO a KeycloakUserRegisterRequest
    public static KeycloakUserRegisterRequest toKeycloakUserRegisterRequest(UserCreateDTO userCreateDTO) {
        if (userCreateDTO == null) {
            return null;
        }
        String lastName = userCreateDTO.lastName();
        if (userCreateDTO.secondLastName() != null && !userCreateDTO.secondLastName().isEmpty()) {
            lastName = lastName + " " + userCreateDTO.secondLastName();
        }
        return new KeycloakUserRegisterRequest(
                userCreateDTO.username(),
                userCreateDTO.email(),
                userCreateDTO.password(),
                userCreateDTO.roles(),
                userCreateDTO.firstName(),
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
