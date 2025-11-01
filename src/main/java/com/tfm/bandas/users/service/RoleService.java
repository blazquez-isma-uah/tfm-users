package com.tfm.bandas.users.service;

import com.tfm.bandas.users.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.users.dto.KeycloakRoleResponse;
import com.tfm.bandas.users.dto.UserResponseDTO;

import java.util.List;

public interface RoleService {
    List<KeycloakRoleResponse> getAllRoles();
    KeycloakRoleResponse createRole(KeycloakRoleRegisterRequest role);
    void deleteRole(String roleName);
    KeycloakRoleResponse getRoleById(String roleId);
    KeycloakRoleResponse getRoleByName(String roleName);
    List<KeycloakRoleResponse> listUserRoles(String userId);
    List<KeycloakRoleResponse> listUserRolesByUsername(String username);
    UserResponseDTO assignRoleToUser(Long userId, String roleName);
    UserResponseDTO removeRoleFromUser(Long userId, String roleName);
    UserResponseDTO updateUserRoles(Long userId, List<String> roleNames);
}
