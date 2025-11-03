package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.client.IdentityClient;
import com.tfm.bandas.users.dto.*;
import com.tfm.bandas.users.dto.mapper.UserProfileMapper;
import com.tfm.bandas.users.exception.BadRequestException;
import com.tfm.bandas.users.exception.NotFoundException;
import com.tfm.bandas.users.model.entity.InstrumentEntity;
import com.tfm.bandas.users.model.entity.UserProfileEntity;
import com.tfm.bandas.users.model.repository.InstrumentRepository;
import com.tfm.bandas.users.model.repository.UserRepository;
import com.tfm.bandas.users.model.specification.UserSpecifications;
import com.tfm.bandas.users.service.RoleService;
import com.tfm.bandas.users.service.UserService;
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
    private final IdentityClient identityClient;
    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(UserProfileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long userId) {
        return UserProfileMapper.toDTO(findUserOrThrow(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByUsername(String username) {
        return userRepo.findByUsername(username) // Asumimos que el username es el email
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByIamId(String iamId) {
        return userRepo.findByIamId(iamId)
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with IAM ID: " + iamId));
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
            KeycloakUserResponse kcUser = identityClient.createUserInKeycloak(UserProfileMapper.toKeycloakUserRegisterRequest(dto));
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
            UserResponseDTO createdUserDTO = UserProfileMapper.toDTO(userProfile);

            // Asignar roles en Keycloak y en base de datos
            if (dto.roles() != null && !dto.roles().isEmpty()) {
                for(String roleName : dto.roles()) {
                    createdUserDTO = roleService.assignRoleToUser(userProfile.getId(), roleName);
                }
            }
            return createdUserDTO;

        } catch (RuntimeException e) {
            if(keycloakId != null) {
                // Intentar limpiar el usuario creado en Keycloak
                try {
                    identityClient.deleteUserByIamId(keycloakId);
                } catch (Exception ex) {
                    // Loggear el error pero no hacer nada más
                    System.err.println("Failed to clean up Keycloak user with IAM ID " + keycloakId + ": " + ex.getMessage());
                }
            }
            throw e; // Re-lanzar la excepción original
        }
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO dto) {
        UserProfileEntity userProfileOriginal = findUserOrThrow(userId);

        // Si el email ha cambiado, hay que verificar que no exista otro usuario con ese email
        if (!userProfileOriginal.getEmail().equals(dto.email())
                && userRepo.existsByEmail(dto.email())) {
            throw new BadRequestException("Another user is already registered with email: " + dto.email());
        }

        KeycloakUserResponse kcUpdatedUser = null;
        try {
            KeycloakUserUpdateRequest kcUserUpdate = new KeycloakUserUpdateRequest(
                    userProfileOriginal.getUsername(),
                    dto.email(),
                    dto.firstName(),
                    dto.lastName() + " " + dto.secondLastName()
            );
            kcUpdatedUser = identityClient.updateUserData(userProfileOriginal.getIamId(), kcUserUpdate);

            UserProfileEntity userProfileToUpdate = updateUserProfileData(dto, userProfileOriginal);
            return UserProfileMapper.toDTO(userRepo.save(userProfileToUpdate));
        } catch (RuntimeException e) {
            if (kcUpdatedUser != null) {
                // Intentar revertir los cambios en Keycloak
                try {
                    KeycloakUserUpdateRequest kcUserRevert = new KeycloakUserUpdateRequest(
                            userProfileOriginal.getUsername(),
                            userProfileOriginal.getEmail(),
                            userProfileOriginal.getFirstName(),
                            userProfileOriginal.getLastName() + " " + userProfileOriginal.getSecondLastName()
                    );
                    identityClient.updateUserData(userProfileOriginal.getIamId(), kcUserRevert);
                } catch (Exception ex) {
                    // Loggear el error pero no hacer nada más
                    System.err.println("Failed to revert Keycloak user with IAM ID " + userProfileOriginal.getIamId() + ": " + ex.getMessage());
                }
            }
            throw e; // Re-lanzar la excepción original
        }
    }

    private UserProfileEntity updateUserProfileData(UserUpdateDTO dto, UserProfileEntity userProfileOriginal) {
        // NO se actualiza id, iamId, systemSignupDate, username → son inmutables
        // NO se actualiza active, roleNames, instruments → se hace con endpoints específicos
        UserProfileEntity userProfileToUpdate = new UserProfileEntity(userProfileOriginal);
        userProfileToUpdate.setFirstName(dto.firstName());
        userProfileToUpdate.setLastName(dto.lastName());
        userProfileToUpdate.setSecondLastName(dto.secondLastName());
        userProfileToUpdate.setPhone(dto.phone());
        userProfileToUpdate.setNotes(dto.notes());
        userProfileToUpdate.setProfilePictureUrl(dto.profilePictureUrl());
        userProfileToUpdate.setBirthDate(dto.birthDate());
        userProfileToUpdate.setBandJoinDate(dto.bandJoinDate());
        userProfileToUpdate.setEmail(dto.email());
        return userProfileToUpdate;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        UserProfileEntity userProfileToDelete = findUserOrThrow(userId);
        if (userProfileToDelete != null) {
            try {
                identityClient.deleteUserByIamId(userProfileToDelete.getIamId());
            } catch (Exception e) {
                throw new RuntimeException("Failed to delete associated Keycloak user with IAM ID: " + userProfileToDelete.getIamId(), e);
            }
        }
        userRepo.deleteById(userId);
    }

    @Override
    @Transactional
    public void disableUser(Long userId) {
        UserProfileEntity user = findUserOrThrow(userId);
        user.setActive(false);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public void enableUser(Long userId) {
        UserProfileEntity user = findUserOrThrow(userId);
        user.setActive(true);
        userRepo.save(user);
    }

    @Override
    @Transactional
    public UserResponseDTO assignInstrumentToUser(Long userId, Long instrumentId) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id " + instrumentId));
        userProfile.getInstruments().add(instrument);
        return UserProfileMapper.toDTO(userRepo.save(userProfile));
    }

    @Override
    @Transactional
    public UserResponseDTO removeInstrumentFromUser(Long userId, Long instrumentId) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id " + instrumentId));
        userProfile.getInstruments().remove(instrument);
        return UserProfileMapper.toDTO(userRepo.save(userProfile));
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserInstruments(Long userId, Set<Long> instrumentIds) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        if (instrumentIds != null) {
            Set<InstrumentEntity> instruments =
                    instrumentIds.isEmpty() ? new HashSet<>() :
                    new HashSet<>(instrumentRepo.findAllById(instrumentIds));
            userProfile.setInstruments(instruments);
        }
        return UserProfileMapper.toDTO(userRepo.save(userProfile));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> searchUsers(String username, String firstName, String lastName, String secondLastName,
                                             String email, Boolean active, Long instrumentId, Pageable pageable) {
        Specification<UserProfileEntity> spec = Specification.allOf(
                UserSpecifications.usernameContains(username),
                UserSpecifications.firstNameContains(firstName),
                UserSpecifications.lastNameContains(lastName),
                UserSpecifications.secondLastNameContains(secondLastName),
                UserSpecifications.emailContains(email),
                UserSpecifications.activeIs(active),
                UserSpecifications.hasInstrument(instrumentId)
        );

        return userRepo.findAll(spec, pageable).map(UserProfileMapper::toDTO);
    }

    private UserProfileEntity findUserOrThrow(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with userId " + userId));
    }

}
