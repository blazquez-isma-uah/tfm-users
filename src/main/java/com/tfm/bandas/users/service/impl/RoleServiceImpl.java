package com.tfm.bandas.users.service.impl;

import com.tfm.bandas.users.client.IdentityFeignClient;
import com.tfm.bandas.users.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.users.dto.KeycloakRoleResponse;
import com.tfm.bandas.users.dto.UserResponseDTO;
import com.tfm.bandas.users.dto.mapper.UserProfileMapper;
import com.tfm.bandas.users.model.entity.UserProfileEntity;
import com.tfm.bandas.users.model.repository.UserRepository;
import com.tfm.bandas.users.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final IdentityFeignClient identityFeignClient;
    private final UserRepository userRepo;

    @Override
    public List<KeycloakRoleResponse> getAllRoles() {
        return identityFeignClient.listAllRoles();
    }

    @Override
    public KeycloakRoleResponse createRole(KeycloakRoleRegisterRequest role) {
        return identityFeignClient.createRealmRole(role);
    }

    @Override
    public void deleteRole(String roleName) {
        identityFeignClient.deleteRealmRole(roleName);
    }

    @Override
    public KeycloakRoleResponse getRoleById(String roleId) {
        return identityFeignClient.getRoleById(roleId);
    }

    @Override
    public KeycloakRoleResponse getRoleByName(String roleName) {
        return identityFeignClient.getRoleByName(roleName);
    }

    @Override
    public List<KeycloakRoleResponse> listUserRoles(String userId) {
        String iamId = userRepo.findById(Long.parseLong(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId))
                .getIamId();
        return identityFeignClient.listUserRoles(iamId);
    }

    @Override
    public List<KeycloakRoleResponse> listUserRolesByUsername(String username) {
        String iamId = userRepo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username))
                .getIamId();
        return identityFeignClient.listUserRoles(iamId);
    }

    @Override
    @Transactional
    public UserResponseDTO assignRoleToUser(Long userId, String roleName) {
        UserProfileEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        identityFeignClient.assignRoleToUser(user.getIamId(), roleName);
        try {
            // Comprobar si tiene ese rol en role_names que es una lista de roles separados por coma y añadirlo si no lo tiene
            String roleNames = user.getRoleNames();
            if (roleNames == null || roleNames.isEmpty()) {
                user.setRoleNames(roleName);
            } else {
                List<String> rolesList = List.of(roleNames.split(","));
                if (!rolesList.contains(roleName)) {
                    user.setRoleNames(roleNames + "," + roleName);
                }
            }
            UserProfileEntity userProfile = userRepo.save(user);
            return UserProfileMapper.toDTO(userProfile);
        } catch (Exception e) {
            // Si se produce un error en la base de datos, desasignar el rol en Keycloak
            identityFeignClient.removeRoleFromUser(user.getIamId(), roleName);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserResponseDTO removeRoleFromUser(Long userId, String roleName) {
        UserProfileEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        identityFeignClient.removeRoleFromUser(user.getIamId(), roleName);
        try {
            // Quitar el rol de role_names
            String roleNames = user.getRoleNames();
            if (roleNames != null && !roleNames.isEmpty()) {
                List<String> rolesList = new ArrayList<>(List.of(roleNames.split(",")));
                if (rolesList.contains(roleName)) {
                    rolesList.remove(roleName);
                    user.setRoleNames(String.join(",", rolesList));
                    UserProfileEntity userProfile = userRepo.save(user);
                    return UserProfileMapper.toDTO(userProfile);
                }
            }
            return UserProfileMapper.toDTO(user);
        } catch (RuntimeException e) {
            // Si se produce un error en la base de datos, reasignar el rol en Keycloak
            identityFeignClient.assignRoleToUser(user.getIamId(), roleName);
            throw e;
        }
    }

    @Override
    @Transactional
    public UserResponseDTO updateUserRoles(Long userId, List<String> roleNames) {
        UserProfileEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        // Obtener los roles actuales del usuario
        List<KeycloakRoleResponse> currentRoles = identityFeignClient.listUserRoles(user.getIamId());
        List<String> currentRoleNames = new ArrayList<>();
        for (KeycloakRoleResponse role : currentRoles) {
            currentRoleNames.add(role.name());
        }
        // Asignar roles que están en roleNames pero no en currentRoleNames
        for (String roleName : roleNames) {
            if (!currentRoleNames.contains(roleName)) {
                identityFeignClient.assignRoleToUser(user.getIamId(), roleName);
            }
        }
        // Quitar roles que están en currentRoleNames pero no en roleNames
        for (String roleName : currentRoleNames) {
            if (!roleNames.contains(roleName)) {
                identityFeignClient.removeRoleFromUser(user.getIamId(), roleName);
            }
        }
        // Actualizar roleNames en la base de datos
        user.setRoleNames(roleNames.isEmpty() ? "" : String.join(",", roleNames));
        return UserProfileMapper.toDTO(userRepo.save(user));
    }
}
