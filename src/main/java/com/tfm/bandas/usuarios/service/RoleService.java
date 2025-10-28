package com.tfm.bandas.usuarios.service;

import com.tfm.bandas.usuarios.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.usuarios.dto.KeycloakRoleResponse;
import com.tfm.bandas.usuarios.dto.UserResponseDTO;

import java.util.List;

public interface RoleService {
    List<KeycloakRoleResponse> getAllRoles();
    KeycloakRoleResponse createRole(KeycloakRoleRegisterRequest role);
    void deleteRole(String roleName);
    KeycloakRoleResponse getRoleById(String id);
    KeycloakRoleResponse getRoleByName(String name);
    List<KeycloakRoleResponse> listUserRoles(String userId);
    List<KeycloakRoleResponse> listUserRolesByUsername(String username);
    UserResponseDTO assignRoleToUser(Long userId, String roleName);
    UserResponseDTO removeRoleFromUser(Long userId, String roleName);
}
