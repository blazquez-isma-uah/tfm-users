package com.tfm.bandas.users.service;

import com.tfm.bandas.users.dto.IdentityRoleRegisterRequest;
import com.tfm.bandas.users.dto.IdentityRoleResponse;
import com.tfm.bandas.users.dto.UserDTO;

import java.util.List;

public interface RoleService {
    List<IdentityRoleResponse> getAllRoles();
    IdentityRoleResponse createRole(IdentityRoleRegisterRequest role);
    void deleteRole(String roleName);
    IdentityRoleResponse getRoleById(String roleId);
    IdentityRoleResponse getRoleByName(String roleName);
    List<IdentityRoleResponse> listUserRoles(String userId);
    List<IdentityRoleResponse> listUserRolesByUsername(String username);
    UserDTO assignRoleToUser(Long userId, String roleName, int ifMatchVersion);
    UserDTO removeRoleFromUser(Long userId, String roleName, int ifMatchVersion);
    UserDTO updateUserRoles(Long userId, List<String> roleNames, int ifMatchVersion);
}
