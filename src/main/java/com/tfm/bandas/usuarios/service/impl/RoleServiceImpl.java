package com.tfm.bandas.usuarios.service.impl;

import com.tfm.bandas.usuarios.client.IdentityClient;
import com.tfm.bandas.usuarios.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.usuarios.dto.KeycloakRoleResponse;
import com.tfm.bandas.usuarios.model.entity.UserProfileEntity;
import com.tfm.bandas.usuarios.model.repository.UserRepository;
import com.tfm.bandas.usuarios.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final IdentityClient identityClient;
    private final UserRepository userRepo;

    @Override
    public List<KeycloakRoleResponse> getAllRoles() {
        return identityClient.listAllRoles();
    }

    @Override
    public KeycloakRoleResponse createRole(KeycloakRoleRegisterRequest role) {
        return identityClient.createRealmRole(role);
    }

    @Override
    public void deleteRole(String roleName) {
        identityClient.deleteRealmRole(roleName);
    }

    @Override
    public KeycloakRoleResponse getRoleById(String id) {
        return identityClient.getRoleById(id);
    }

    @Override
    public KeycloakRoleResponse getRoleByName(String name) {
        return identityClient.getRoleByName(name);
    }

    @Override
    public List<KeycloakRoleResponse> listUserRoles(String userId) {
        return identityClient.listUserRoles(userId);
    }

    @Override
    @Transactional
    public void assignRealmRole(Long userId, String roleName) {
        UserProfileEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        identityClient.assignRealmRole(user.getIamId(), roleName);

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
            userRepo.save(user);
        } catch (Exception e) {
            // Si se produce un error en la base de datos, desasignar el rol en Keycloak
            identityClient.removeRealmRole(user.getIamId(), roleName);
            throw e;
        }

    }

    @Override
    public void removeRealmRole(Long userId, String roleName) {
        UserProfileEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        identityClient.removeRealmRole(user.getIamId(), roleName);
        try {
            // Quitar el rol de role_names
            String roleNames = user.getRoleNames();
            if (roleNames != null && !roleNames.isEmpty()) {
                List<String> rolesList = new ArrayList<>(List.of(roleNames.split(",")));
                if (rolesList.contains(roleName)) {
                    rolesList.remove(roleName);
                    user.setRoleNames(String.join(",", rolesList));
                    userRepo.save(user);
                }
            }
        } catch (RuntimeException e) {
            // Si se produce un error en la base de datos, reasignar el rol en Keycloak
            identityClient.assignRealmRole(user.getIamId(), roleName);
            throw e;
        }
    }
}
