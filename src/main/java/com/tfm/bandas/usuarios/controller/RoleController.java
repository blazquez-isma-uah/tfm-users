package com.tfm.bandas.usuarios.controller;

import com.tfm.bandas.usuarios.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.usuarios.dto.KeycloakRoleResponse;
import com.tfm.bandas.usuarios.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public List<KeycloakRoleResponse> getAllRoles() {
        return roleService.getAllRoles();
    }

    @PostMapping
    public KeycloakRoleResponse createRole(@RequestBody KeycloakRoleRegisterRequest role) {
        return roleService.createRole(role);
    }

    @DeleteMapping("/{roleName}")
    public void deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);
    }

    @GetMapping("/{id}")
    public KeycloakRoleResponse getRoleById(@PathVariable String id) {
        return roleService.getRoleById(id);
    }

    @GetMapping("/name/{name}")
    public KeycloakRoleResponse getRoleByName(@PathVariable String name) {
        return roleService.getRoleByName(name);
    }

    @GetMapping("/user/{userId}")
    public List<KeycloakRoleResponse> listUserRoles(@PathVariable String userId) {
        return roleService.listUserRoles(userId);
    }

    @PostMapping("/user/{userId}/{role}")
    public void assignRealmRole(@PathVariable Long userId, @PathVariable String role) {
        roleService.assignRealmRole(userId, role);
    }

    @DeleteMapping("/user/{userId}/{role}")
    public void removeRealmRole(@PathVariable Long userId, @PathVariable String role) {
        roleService.removeRealmRole(userId, role);
    }
}
