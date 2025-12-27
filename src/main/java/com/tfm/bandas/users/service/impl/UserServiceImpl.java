package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.client.IdentityFeignClient;
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
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.tfm.bandas.users.utils.EtagUtils.compareVersion;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final InstrumentRepository instrumentRepo;
    private final IdentityFeignClient identityFeignClient;
    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepo.findAll(pageable)
                .map(UserProfileMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long userId) {
        return UserProfileMapper.toDTO(findUserOrThrow(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        return userRepo.findByUsername(username) // Asumimos que el username es el email
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByIamId(String iamId) {
        return userRepo.findByIamId(iamId)
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("User not found with IAM ID: " + iamId));
    }

    @Override
    @Transactional
    public UserDTO createUser(UserCreateRequestDTO dto) {
        if(userRepo.existsByUsername(dto.username())) {
            throw new BadRequestException("User already registered with username: " + dto.username());
        }
        if (userRepo.existsByEmail(dto.email())) {
            throw new BadRequestException("User already registered with email: " + dto.email());
        }

        String keycloakId = null;
        try {
            KeycloakUserResponse kcUser = identityFeignClient.createUserInKeycloak(UserProfileMapper.toKeycloakUserRegisterRequest(dto));
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
            userProfile = userRepo.saveAndFlush(userProfile);
            UserDTO createdUserDTO = UserProfileMapper.toDTO(userProfile);

            // Asignar roles en Keycloak y en base de datos
            if (dto.roles() != null && !dto.roles().isEmpty()) {
                for(String roleName : dto.roles()) {
                    createdUserDTO = roleService.assignRoleToUser(userProfile.getId(), roleName, createdUserDTO.version());
                }
            }
            return createdUserDTO;

        } catch (RuntimeException e) {
            if(keycloakId != null) {
                // Intentar limpiar el usuario creado en Keycloak
                try {
                    identityFeignClient.deleteUserByIamId(keycloakId);
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
    public UserDTO updateUser(Long userId, UserUpdateRequestDTO dto, int ifMatchVersion) {
        UserProfileEntity userProfileOriginal = findUserOrThrow(userId);

        // Si el email ha cambiado, hay que verificar que no exista otro usuario con ese email
        if (!userProfileOriginal.getEmail().equals(dto.email())
                && userRepo.existsByEmail(dto.email())) {
            throw new BadRequestException("Another user is already registered with email: " + dto.email());
        }

        compareVersion(ifMatchVersion, userProfileOriginal.getVersion());

        KeycloakUserResponse kcUpdatedUser = null;
        try {
            KeycloakUserUpdateRequest kcUserUpdate = new KeycloakUserUpdateRequest(
                    userProfileOriginal.getUsername(),
                    dto.email(),
                    dto.firstName(),
                    dto.lastName() + " " + dto.secondLastName()
            );
            kcUpdatedUser = identityFeignClient.updateUserData(userProfileOriginal.getIamId(), kcUserUpdate);

            updateUserProfileData(dto, userProfileOriginal);
            return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfileOriginal));
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
                    identityFeignClient.updateUserData(userProfileOriginal.getIamId(), kcUserRevert);
                } catch (Exception ex) {
                    // Loggear el error pero no hacer nada más
                    System.err.println("Failed to revert Keycloak user with IAM ID " + userProfileOriginal.getIamId() + ": " + ex.getMessage());
                }
            }
            throw e; // Re-lanzar la excepción original
        }
    }

    private void updateUserProfileData(UserUpdateRequestDTO dto, UserProfileEntity userProfile) {
        // NO se actualiza id, iamId, systemSignupDate, username → son inmutables
        // NO se actualiza active, roleNames, instruments → se hace con endpoints específicos
        userProfile.setFirstName(dto.firstName());
        userProfile.setLastName(dto.lastName());
        userProfile.setSecondLastName(dto.secondLastName());
        userProfile.setPhone(dto.phone());
        userProfile.setNotes(dto.notes());
        userProfile.setProfilePictureUrl(dto.profilePictureUrl());
        userProfile.setBirthDate(dto.birthDate());
        userProfile.setBandJoinDate(dto.bandJoinDate());
        userProfile.setEmail(dto.email());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, int ifMatchVersion) {
        UserProfileEntity userProfileToDelete = findUserOrThrow(userId);
        if (userProfileToDelete != null) {
            compareVersion(ifMatchVersion, userProfileToDelete.getVersion());
            try {
                identityFeignClient.deleteUserByIamId(userProfileToDelete.getIamId());
            } catch (FeignException fe) {
                // Propagar FeignException para que el handler lo traduzca según el upstream
                throw fe;
            } catch (Exception e) {
                throw new BadRequestException("Failed to delete associated Keycloak user with IAM ID: " + userProfileToDelete.getIamId() + ". Cause: " + e.getMessage());
            }
        }
        userRepo.deleteById(userId);
    }

    @Override
    @Transactional
    public void disableUser(Long userId, int ifMatchVersion) {
        UserProfileEntity user = findUserOrThrow(userId);
        compareVersion(ifMatchVersion, user.getVersion());
        user.setActive(false);
        userRepo.saveAndFlush(user);
    }

    @Override
    @Transactional
    public void enableUser(Long userId, int ifMatchVersion) {
        UserProfileEntity user = findUserOrThrow(userId);
        compareVersion(ifMatchVersion, user.getVersion());
        user.setActive(true);
        userRepo.saveAndFlush(user);
    }

    @Override
    @Transactional
    public UserDTO assignInstrumentToUser(Long userId, Long instrumentId, int ifMatchVersion) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id " + instrumentId));
        compareVersion(ifMatchVersion, userProfile.getVersion());
        userProfile.getInstruments().add(instrument);
        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    @Override
    @Transactional
    public UserDTO removeInstrumentFromUser(Long userId, Long instrumentId, int ifMatchVersion) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("Instrument not found with id " + instrumentId));
        compareVersion(ifMatchVersion, userProfile.getVersion());
        userProfile.getInstruments().remove(instrument);
        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    @Override
    @Transactional
    public UserDTO updateUserInstruments(Long userId, Set<Long> instrumentIds, int ifMatchVersion) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        if (instrumentIds != null) {
            Set<InstrumentEntity> instruments =
                    instrumentIds.isEmpty() ? new HashSet<>() :
                    new HashSet<>(instrumentRepo.findAllById(instrumentIds));
            userProfile.setInstruments(instruments);
        }
        compareVersion(ifMatchVersion, userProfile.getVersion());
        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String username, String firstName, String lastName, String secondLastName,
                                     String email, Boolean active, Long instrumentId, String role,
                                     LocalDate birthDateFrom, LocalDate birthDateTo,
                                     LocalDate bandJoinDateFrom, LocalDate bandJoinDateTo,
                                     Pageable pageable) {
        Specification<UserProfileEntity> spec = Specification.allOf(
                UserSpecifications.usernameContains(username),
                UserSpecifications.firstNameContains(firstName),
                UserSpecifications.lastNameContains(lastName),
                UserSpecifications.secondLastNameContains(secondLastName),
                UserSpecifications.emailContains(email),
                UserSpecifications.activeIs(active),
                UserSpecifications.hasInstrument(instrumentId),
                UserSpecifications.hasRole(role),
                UserSpecifications.birthDateBetween(birthDateFrom, birthDateTo),
                UserSpecifications.bandJoinDateBetween(bandJoinDateFrom, bandJoinDateTo)
        );

        return userRepo.findAll(spec, pageable).map(UserProfileMapper::toDTO);
    }

    @Override
    @Transactional
    public void updateMyPassword(String iamId, String newPassword) {
        // Verificar que el usuario existe en la base de datos local
        userRepo.findByIamId(iamId)
                .orElseThrow(() -> new NotFoundException("User not found with IAM ID: " + iamId));

        // Actualizar la contraseña en Keycloak
        try {
            KeycloakUserPasswordUpdateRequest request = new KeycloakUserPasswordUpdateRequest(newPassword);
            identityFeignClient.updateUserPassword(iamId, request);
        } catch (FeignException fe) {
            throw fe;
        } catch (Exception e) {
            throw new BadRequestException("Failed to update password in Keycloak. Cause: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserDTO updateMyProfile(String iamId, MyProfileUpdateRequestDTO dto) {
        // Buscar el usuario por iamId
        UserProfileEntity userProfile = userRepo.findByIamId(iamId)
                .orElseThrow(() -> new NotFoundException("User not found with IAM ID: " + iamId));

        // Actualizar solo los campos permitidos
        if (dto.firstName() != null) {
            userProfile.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            userProfile.setLastName(dto.lastName());
        }
        if (dto.secondLastName() != null) {
            userProfile.setSecondLastName(dto.secondLastName());
        }
        if (dto.phone() != null) {
            userProfile.setPhone(dto.phone());
        }
        if (dto.notes() != null) {
            userProfile.setNotes(dto.notes());
        }
        if (dto.profilePictureUrl() != null) {
            userProfile.setProfilePictureUrl(dto.profilePictureUrl());
        }
        if (dto.birthDate() != null) {
            userProfile.setBirthDate(dto.birthDate());
        }

        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    private UserProfileEntity findUserOrThrow(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with userId " + userId));
    }

}
