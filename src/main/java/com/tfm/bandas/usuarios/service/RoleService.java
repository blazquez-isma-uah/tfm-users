package com.tfm.bandas.usuarios.service;

import com.tfm.bandas.usuarios.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.usuarios.dto.KeycloakRoleResponse;
import java.util.List;

public interface RoleService {
    List<KeycloakRoleResponse> getAllRoles();
    KeycloakRoleResponse createRole(KeycloakRoleRegisterRequest role);
    void deleteRole(String roleName);
    KeycloakRoleResponse getRoleById(String id);
    KeycloakRoleResponse getRoleByName(String name);
    List<KeycloakRoleResponse> listUserRoles(String userId);
    void assignRealmRole(Long userId, String roleName);
    void removeRealmRole(Long userId, String roleName);
}
