package com.tfm.bandas.users.service;

import com.tfm.bandas.users.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.users.dto.KeycloakRoleResponse;
import com.tfm.bandas.users.dto.UserDTO;

import java.util.List;

public interface RoleService {
    List<KeycloakRoleResponse> getAllRoles();
    KeycloakRoleResponse createRole(KeycloakRoleRegisterRequest role);
    void deleteRole(String roleName);
    KeycloakRoleResponse getRoleById(String roleId);
    KeycloakRoleResponse getRoleByName(String roleName);
    List<KeycloakRoleResponse> listUserRoles(String userId);
    List<KeycloakRoleResponse> listUserRolesByUsername(String username);
    UserDTO assignRoleToUser(Long userId, String roleName, int ifMatchVersion);
    UserDTO removeRoleFromUser(Long userId, String roleName, int ifMatchVersion);
    UserDTO updateUserRoles(Long userId, List<String> roleNames, int ifMatchVersion);
}
