package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.client.IdentityFeignClient;
import com.tfm.bandas.users.dto.*;
import com.tfm.bandas.users.dto.mapper.UserProfileMapper;
import com.tfm.bandas.users.exception.BadRequestException;
import com.tfm.bandas.users.exception.ConflictException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
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
                .orElseThrow(() -> new NotFoundException("No se encontró ningún usuario con el email " + email + "."));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        return userRepo.findByUsername(username) // Asumimos que el username es el email
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún usuario con el nombre de usuario " + username + "."));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByIamId(String iamId) {
        return userRepo.findByIamId(iamId)
                .map(UserProfileMapper::toDTO)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún usuario con el IAM ID " + iamId + "."));
    }

    @Override
    @Transactional
    public UserDTO createUser(UserCreateRequestDTO dto) {
        if(userRepo.existsByUsername(dto.username())) {
            throw new ConflictException("Ya existe un usuario registrado con el nombre de usuario " + dto.username(), "USERNAME_EXISTS");
        }
        if (userRepo.existsByEmail(dto.email())) {
            throw new ConflictException("Ya existe un usuario registrado con el email " + dto.email(), "EMAIL_EXISTS");
        }

        String iamId = null;
        try {
            IdentityUserResponse identityUser = identityFeignClient.createUser(UserProfileMapper.toIdentityUserRegisterRequest(dto));
            iamId = identityUser.id();
            UserProfileEntity userProfile = UserProfileEntity.builder()
                    .iamId(iamId)
                    .systemSignupDate(dto.systemSignupDate() != null ? dto.systemSignupDate() : LocalDate.now())
                    .active(true)
                    .username(dto.username())
                    .firstName(dto.firstName())
                    .lastName(dto.lastName())
                    .secondLastName(dto.secondLastName())
                    .email(dto.email())
                    .phone(dto.phone())
                    .notes(dto.notes())
                    .birthDate(dto.birthDate())
                    .bandJoinDate(dto.bandJoinDate())
                    .build();

            if (dto.instrumentIds() != null) {
                Set<InstrumentEntity> instruments = new HashSet<>(instrumentRepo.findAllById(dto.instrumentIds()));
                userProfile.setInstruments(instruments);
            }
            userProfile = userRepo.saveAndFlush(userProfile);
            UserDTO createdUserDTO = UserProfileMapper.toDTO(userProfile);

            // Asignar roles en Identity y en base de datos
            if (dto.roles() != null && !dto.roles().isEmpty()) {
                for(String roleName : dto.roles()) {
                    createdUserDTO = roleService.assignRoleToUser(userProfile.getId(), roleName, createdUserDTO.version());
                    // Refrescar la entidad desde BD para obtener la versión actualizada
                    userProfile = userRepo.findById(userProfile.getId()).orElse(userProfile);
                }
            }
            return createdUserDTO;

        } catch (RuntimeException e) {
            if(iamId != null) {
                // Intentar limpiar el usuario creado en Identity
                try {
                    identityFeignClient.deleteUserById(iamId);
                } catch (Exception ex) {
                    logger.error("Failed to clean up Identity user with IAM ID {}: {}", iamId, ex.getMessage(), ex);
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
            throw new ConflictException("Ya existe otro usuario registrado con el email " + dto.email(), "EMAIL_EXISTS");
        }

        compareVersion(ifMatchVersion, userProfileOriginal.getVersion());

        String originalEmail = userProfileOriginal.getEmail();
        String originalFirstName = userProfileOriginal.getFirstName();
        String originalLastName = userProfileOriginal.getLastName();
        String originalSecondLastName = userProfileOriginal.getSecondLastName();

        IdentityUserResponse kcUpdatedUser = null;
        try {
            IdentityUserUpdateRequest kcUserUpdate = new IdentityUserUpdateRequest(
                    dto.email(),
                    dto.firstName(),
                    buildFullLastName(dto.lastName(), dto.secondLastName())
            );
            kcUpdatedUser = identityFeignClient.updateUserData(userProfileOriginal.getIamId(), kcUserUpdate);

            updateUserProfileData(dto, userProfileOriginal);
            return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfileOriginal));
        } catch (RuntimeException e) {
            if (kcUpdatedUser != null) {
                // Intentar revertir los cambios en Identity usando los valores originales
                try {
                    IdentityUserUpdateRequest kcUserRevert = new IdentityUserUpdateRequest(
                            originalEmail,
                            originalFirstName,
                            buildFullLastName(originalLastName, originalSecondLastName)
                    );
                    identityFeignClient.updateUserData(userProfileOriginal.getIamId(), kcUserRevert);
                } catch (Exception ex) {
                    logger.error("Failed to revert Identity user with IAM ID {}: {}", userProfileOriginal.getIamId(), ex.getMessage(), ex);
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
        userProfile.setBirthDate(dto.birthDate());
        userProfile.setBandJoinDate(dto.bandJoinDate());
        userProfile.setEmail(dto.email());
    }

    /**
     * Combina primer y segundo apellido en un único campo para el proveedor de identidad.
     * Cognito y Keycloak solo tienen un campo family_name, por lo que ambos apellidos
     * se almacenan concatenados en ese campo.
     */
    private String buildFullLastName(String lastName, String secondLastName) {
        if (lastName == null || lastName.isEmpty()) {
            return secondLastName != null ? secondLastName : "";
        }
        if (secondLastName == null || secondLastName.isEmpty()) {
            return lastName;
        }
        return lastName + " " + secondLastName;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, int ifMatchVersion) {
        UserProfileEntity userProfileToDelete = findUserOrThrow(userId);
        compareVersion(ifMatchVersion, userProfileToDelete.getVersion());
        try {
            identityFeignClient.deleteUserById(userProfileToDelete.getIamId());
        } catch (FeignException fe) {
            throw fe;
        } catch (Exception e) {
            logger.error("Failed to delete identity provider user with IAM ID {}: {}", userProfileToDelete.getIamId(), e.getMessage(), e);
            throw new BadRequestException("No se ha podido eliminar el usuario en el proveedor de identidad.");
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
                .orElseThrow(() -> new NotFoundException("No se encontró ningún instrumento con el ID " + instrumentId + "."));
        compareVersion(ifMatchVersion, userProfile.getVersion());
        userProfile.getInstruments().add(instrument);
        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    @Override
    @Transactional
    public UserDTO removeInstrumentFromUser(Long userId, Long instrumentId, int ifMatchVersion) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        InstrumentEntity instrument = instrumentRepo.findById(instrumentId)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún instrumento con el ID " + instrumentId + "."));
        compareVersion(ifMatchVersion, userProfile.getVersion());
        userProfile.getInstruments().remove(instrument);
        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    @Override
    @Transactional
    public UserDTO updateUserInstruments(Long userId, Set<Long> instrumentIds, int ifMatchVersion) {
        UserProfileEntity userProfile = findUserOrThrow(userId);
        if (instrumentIds != null) {
            Set<InstrumentEntity> instruments = instrumentIds.isEmpty()
                    ? new HashSet<>()
                    : new HashSet<>(instrumentRepo.findAllById(instrumentIds));
            userProfile.setInstruments(instruments);
        }
        compareVersion(ifMatchVersion, userProfile.getVersion());
        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String username, String firstName, String lastName,
                                     String secondLastName, String email, Boolean active,
                                     Long instrumentId, String role,
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
                .orElseThrow(() -> new NotFoundException("No se encontró ningún usuario con el IAM ID " + iamId + "."));

        // Actualizar la contraseña en Identity
        try {
            identityFeignClient.updateUserPassword(iamId, new IdentityUserPasswordUpdateRequest(newPassword));
        } catch (FeignException fe) {
            // Propagar FeignException para que el handler lo traduzca según el upstream
            throw fe;
        } catch (Exception e) {
            logger.error("Failed to update password in identity provider for IAM ID {}: {}", iamId, e.getMessage(), e);
            throw new BadRequestException("No se ha podido actualizar la contraseña en el proveedor de identidad.");
        }
    }

    @Override
    @Transactional
    public UserDTO updateMyProfile(String iamId, MyProfileUpdateRequestDTO dto) {
        // Buscar el usuario por iamId
        UserProfileEntity userProfile = userRepo.findByIamId(iamId)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún usuario con el IAM ID " + iamId + "."));

        if (dto.firstName() != null) userProfile.setFirstName(dto.firstName());
        if (dto.lastName() != null) userProfile.setLastName(dto.lastName());
        if (dto.secondLastName() != null) userProfile.setSecondLastName(dto.secondLastName());
        if (dto.phone() != null) userProfile.setPhone(dto.phone());
        if (dto.notes() != null) userProfile.setNotes(dto.notes());
        if (dto.birthDate() != null) userProfile.setBirthDate(dto.birthDate());

        return UserProfileMapper.toDTO(userRepo.saveAndFlush(userProfile));
    }

    private UserProfileEntity findUserOrThrow(Long userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("No se encontró ningún usuario con el ID " + userId + "."));
    }

}
