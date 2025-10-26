package com.tfm.bandas.usuarios.controller;

import com.tfm.bandas.usuarios.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.usuarios.dto.KeycloakRoleResponse;
import com.tfm.bandas.usuarios.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;

    @GetMapping
    public List<KeycloakRoleResponse> getAllRoles() {
        logger.info("Calling getAllRoles");
        List<KeycloakRoleResponse> response = roleService.getAllRoles();
        logger.info("getAllRoles returning: {}", response);
        return response;
    }

    @PostMapping
    public KeycloakRoleResponse createRole(@RequestBody KeycloakRoleRegisterRequest role) {
        logger.info("Calling createRole with role: {}", role);
        KeycloakRoleResponse response = roleService.createRole(role);
        logger.info("createRole returning: {}", response);
        return response;
    }

    @DeleteMapping("/{roleName}")
    public void deleteRole(@PathVariable String roleName) {
        logger.info("Calling deleteRole with roleName: {}", roleName);
        roleService.deleteRole(roleName);
        logger.info("deleteRole completed for roleName: {}", roleName);
    }

    @GetMapping("/{id}")
    public KeycloakRoleResponse getRoleById(@PathVariable String id) {
        logger.info("Calling getRoleById with id: {}", id);
        KeycloakRoleResponse response = roleService.getRoleById(id);
        logger.info("getRoleById returning: {}", response);
        return response;
    }

    @GetMapping("/name/{name}")
    public KeycloakRoleResponse getRoleByName(@PathVariable String name) {
        logger.info("Calling getRoleByName with name: {}", name);
        KeycloakRoleResponse response = roleService.getRoleByName(name);
        logger.info("getRoleByName returning: {}", response);
        return response;
    }

    @GetMapping("/user/{userId}")
    public List<KeycloakRoleResponse> listUserRoles(@PathVariable String userId) {
        logger.info("Calling listUserRoles with userId: {}", userId);
        List<KeycloakRoleResponse> response = roleService.listUserRoles(userId);
        logger.info("listUserRoles returning: {}", response);
        return response;
    }

    @PostMapping("/user/{userId}/{role}")
    public void assignRealmRole(@PathVariable Long userId, @PathVariable String role) {
        logger.info("Calling assignRealmRole with userId: {}, role: {}", userId, role);
        roleService.assignRealmRole(userId, role);
        logger.info("assignRealmRole completed for userId: {}, role: {}", userId, role);
    }

    @DeleteMapping("/user/{userId}/{role}")
    public void removeRealmRole(@PathVariable Long userId, @PathVariable String role) {
        logger.info("Calling removeRealmRole with userId: {}, role: {}", userId, role);
        roleService.removeRealmRole(userId, role);
        logger.info("removeRealmRole completed for userId: {}, role: {}", userId, role);
    }
}
