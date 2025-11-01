package com.tfm.bandas.users.dto.mapper;

import com.tfm.bandas.users.dto.KeycloakUserRegisterRequest;
import com.tfm.bandas.users.dto.UserCreateDTO;
import com.tfm.bandas.users.dto.UserResponseDTO;
import com.tfm.bandas.users.model.entity.InstrumentEntity;
import com.tfm.bandas.users.model.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    //en la entity, el campo role_name es un string de roles separados por comas, en el dto es un set de strings
    @Mapping(target = "instruments", source = "instruments")
    @Mapping(target = "roles", source = "roleNames")
    UserResponseDTO toDTO(UserProfileEntity userProfile);

    UserProfileEntity toEntityFromCreateDTO(UserCreateDTO userCreateDTO);

    // LastName en KeycloakUserRegisterRequest se obtiene de unir lastName + " " + secondLastName de UserCreateDTO
    @Mapping(target = "lastName", expression = "java(userCreateDTO.lastName() + \" \" + userCreateDTO.secondLastName())")
    KeycloakUserRegisterRequest toKeycloakUserRegisterRequest(UserCreateDTO userCreateDTO);

    // Métodos auxiliares que MapStruct utilizará automáticamente
    default Set<String> mapInstruments(Set<InstrumentEntity> instruments) {
        return instruments.stream()
                .map(i -> i.getInstrumentName() + " " + i.getVoice())
                .collect(Collectors.toSet());
    }

    // Convierte "ROLE_A,ROLE_B" -> Set<"ROLE_A","ROLE_B">
    default Set<String> mapRoleNamesToSet(String roleNames) {
        if (roleNames == null || roleNames.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(roleNames.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    // Convierte Set<"ROLE_A","ROLE_B"> -> "ROLE_A,ROLE_B"
    default String mapSetToRoleNames(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        return roles.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));
    }
}
