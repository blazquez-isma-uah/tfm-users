package com.tfm.bandas.usuarios.service.impl;

import com.tfm.bandas.usuarios.client.IdentityClient;
import com.tfm.bandas.usuarios.dto.KeycloakUserResponse;
import com.tfm.bandas.usuarios.dto.UserCreateDTO;
import com.tfm.bandas.usuarios.dto.UserResponseDTO;
import com.tfm.bandas.usuarios.dto.mapper.UserProfileMapper;
import com.tfm.bandas.usuarios.exception.BadRequestException;
import com.tfm.bandas.usuarios.exception.NotFoundException;
import com.tfm.bandas.usuarios.model.entity.InstrumentEntity;
import com.tfm.bandas.usuarios.model.entity.UserProfileEntity;
import com.tfm.bandas.usuarios.model.repository.InstrumentRepository;
import com.tfm.bandas.usuarios.model.repository.UserRepository;
import com.tfm.bandas.usuarios.model.specification.UserSpecifications;
import com.tfm.bandas.usuarios.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final InstrumentRepository instrumentRepo;
    private final UserProfileMapper userProfileMapper;
    private final IdentityClient identityClient;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(userProfileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return userRepo.findById(id)
                .map(userProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(userProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        return userRepo.findByUsername(username) // Asumimos que el username es el email
                .map(userProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByIamId(String iamId) {
        return userRepo.findByIamId(iamId)
                .map(userProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with IAM id: " + iamId));
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(UserCreateDTO dto) {
        if(userRepo.existsByUsername(dto.username())) {
            throw new BadRequestException("User already registered with username: " + dto.username());
        }
        if (userRepo.existsByEmail(dto.email())) {
            throw new BadRequestException("User already registered with email: " + dto.email());
        }

        String keycloakId = null;
        try {
            KeycloakUserResponse kcUser = identityClient.createUserInKeycloak(userProfileMapper.toKeycloakUserRegisterRequest(dto));
            keycloakId = kcUser.id();

            UserProfileEntity userProfile = UserProfileEntity.builder()
                    .iamId(keycloakId)
                    .systemSignupDate(dto.systemSignupDate() != null ? dto.systemSignupDate() : LocalDate.now())
                    .active(true)
                    .username(dto.username())
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .secondLastName(dto.secondLastName())
                    .email(dto.email())
                    .phone(dto.phone())
                    .notes(dto.notes())
                    .profilePictureUrl(dto.profilePictureUrl())
                    .birthDate(dto.birthDate())
                    .bandJoinDate(dto.bandJoinDate())
                    .build();

            if (dto.instrumentIds() != null) {
                Set<InstrumentEntity> instruments = new HashSet<>(instrumentRepo.findAllById(dto.instrumentIds()));
                userProfile.setInstruments(instruments);
            }

            userProfile = userRepo.save(userProfile);
            return userProfileMapper.toDTO(userProfile);

        } catch (RuntimeException e) {
            if(keycloakId != null) {
                // Intentar limpiar el usuario creado en Keycloak
                try {
                    identityClient.deleteUserInKeycloak(keycloakId);
                } catch (Exception ex) {
                    // Loggear el error pero no hacer nada más
                    System.err.println("Failed to clean up Keycloak user with id " + keycloakId + ": " + ex.getMessage());
                }
            }
            throw e; // Re-lanzar la excepción original
        }
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long id, UserCreateDTO dto) {
        UserProfileEntity userProfile = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        // NO se actualiza iamId → es inmutable
        // NO se actualiza systemSignupDate → es inmutable
        // NO se actualiza active → se hace con endpoints específicos
        // Email y nombres: se supone que ya han sido cambiados en el IdP y se sincronizan aquí
        setMainUserInfo(dto, userProfile);

        return userProfileMapper.toDTO(userRepo.save(userProfile));
    }

    private void setMainUserInfo(UserCreateDTO dto, UserProfileEntity userProfile) {
        userProfile.setUsername(dto.username());
        userProfile.setFirstName(dto.firstName());
        userProfile.setLastName(dto.lastName());
        userProfile.setSecondLastName(dto.secondLastName());
        userProfile.setEmail(dto.email());
        userProfile.setPhone(dto.phone());
        userProfile.setNotes(dto.notes());
        userProfile.setProfilePictureUrl(dto.profilePictureUrl());
        userProfile.setBirthDate(dto.birthDate());
        userProfile.setBandJoinDate(dto.bandJoinDate());

        if (dto.instrumentIds() != null) {
            Set<InstrumentEntity> instruments = new HashSet<>(instrumentRepo.findAllById(dto.instrumentIds()));
            userProfile.setInstruments(instruments);
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new NotFoundException("User not found with id " + id);
        }
        userRepo.deleteById(id);
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        UserProfileEntity user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
        user.setActive(false);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        UserProfileEntity user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
        user.setActive(true);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserInstruments(Long userId, Set<Long> instrumentIds) {
        UserProfileEntity userProfile = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        if (instrumentIds != null && !instrumentIds.isEmpty()) {
            Set<InstrumentEntity> instruments = new HashSet<>(instrumentRepo.findAllById(instrumentIds));
            userProfile.setInstruments(instruments);
        }
        return userProfileMapper.toDTO(userRepo.save(userProfile));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String username, String firstName, String lastName, String email, Boolean active, Long instrumentId, Pageable pageable) {
        Specification<UserProfileEntity> spec = Specification.allOf(
                UserSpecifications.usernameContains(username),
                UserSpecifications.firstNameContains(firstName),
                UserSpecifications.lastNameContains(lastName),
                UserSpecifications.emailContains(email),
                UserSpecifications.activeIs(active),
                UserSpecifications.hasInstrument(instrumentId)
        );

        return userRepo.findAll(spec, pageable).map(userProfileMapper::toDTO);
    }

}
