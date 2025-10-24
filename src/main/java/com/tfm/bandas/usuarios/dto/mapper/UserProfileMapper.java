package com.tfm.bandas.usuarios.dto.mapper;

import com.tfm.bandas.usuarios.dto.KeycloakUserRegisterRequest;
import com.tfm.bandas.usuarios.dto.UserCreateDTO;
import com.tfm.bandas.usuarios.dto.UserResponseDTO;
import com.tfm.bandas.usuarios.model.entity.InstrumentEntity;
import com.tfm.bandas.usuarios.model.entity.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {
    @Mapping(target = "instruments", source = "instruments")
    UserResponseDTO toDTO(UserProfileEntity userProfile);
    // Mapear a UserCreateDTO
    UserProfileEntity toEntityFromCreateDTO(UserCreateDTO userCreateDTO);

    // Mapear a KeycloakUserRegisterRequest
    // LastName en KeycloakUserRegisterRequest se obtiene de unir lastName + " " + secondLastName de UserCreateDTO
    @Mapping(target = "lastName", expression = "java(userCreateDTO.lastName() + \" \" + userCreateDTO.secondLastName())")
    KeycloakUserRegisterRequest toKeycloakUserRegisterRequest(UserCreateDTO userCreateDTO);

    // Métodos auxiliares que MapStruct utilizará automáticamente
    default Set<String> mapInstruments(Set<InstrumentEntity> instruments) {
        return instruments.stream()
                .map(i -> i.getInstrumentName() + " " + i.getVoice())
                .collect(Collectors.toSet());
    }
}
