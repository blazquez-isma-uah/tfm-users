package com.tfm.bandas.users.controller;

import com.tfm.bandas.users.dto.KeycloakRoleRegisterRequest;
import com.tfm.bandas.users.dto.KeycloakRoleResponse;
import com.tfm.bandas.users.dto.UserDTO;
import com.tfm.bandas.users.service.RoleService;
import com.tfm.bandas.users.utils.EtagUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<KeycloakRoleResponse>> getAllRoles() {
        logger.info("Calling getAllRoles");
        List<KeycloakRoleResponse> response = roleService.getAllRoles();
        logger.info("getAllRoles returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<KeycloakRoleResponse> createRole(@RequestBody KeycloakRoleRegisterRequest role) {
        logger.info("Calling createRole with role: {}", role);
        KeycloakRoleResponse response = roleService.createRole(role);
        logger.info("createRole returning: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{roleName}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleName) {
        logger.info("Calling deleteRole with roleName: {}", roleName);
        roleService.deleteRole(roleName);
        logger.info("deleteRole completed for roleName: {}", roleName);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<KeycloakRoleResponse> getRoleById(@PathVariable String roleId) {
        logger.info("Calling getRoleById with roleId: {}", roleId);
        KeycloakRoleResponse response = roleService.getRoleById(roleId);
        logger.info("getRoleById returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/name/{roleName}")
    public ResponseEntity<KeycloakRoleResponse> getRoleByName(@PathVariable String roleName) {
        logger.info("Calling getRoleByName with roleName: {}", roleName);
        KeycloakRoleResponse response = roleService.getRoleByName(roleName);
        logger.info("getRoleByName returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<KeycloakRoleResponse>> listUserRoles(@PathVariable String userId) {
        logger.info("Calling listUserRoles with userId: {}", userId);
        List<KeycloakRoleResponse> response = roleService.listUserRoles(userId);
        logger.info("listUserRoles returning: {}", response);
        return ResponseEntity.ok(response);
    }

    // listUserRoles by Username
    @GetMapping("/user/username/{username}")
    public ResponseEntity<List<KeycloakRoleResponse>> listUserRolesByUsername(@PathVariable String username) {
        logger.info("Calling listUserRolesByUsername with username: {}", username);
        List<KeycloakRoleResponse> response = roleService.listUserRolesByUsername(username);
        logger.info("listUserRolesByUsername returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<UserDTO> updateUserRoles(@PathVariable Long userId, @RequestBody List<String> roleNames,
                                                   @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling updateUserRoles with userId: {} and roleNames: {}, ifMatch: {}", userId, roleNames, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        UserDTO response = roleService.updateUserRoles(userId, roleNames, version);
        logger.info("updateUserRoles returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/user/{userId}/{roleName}")
    public ResponseEntity<UserDTO> assignRoleToUser(@PathVariable Long userId, @PathVariable String roleName,
                                                    @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling assignRealmRole with userId: {}, roleName: {}, ifMatch: {}", userId, roleName, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        UserDTO response = roleService.assignRoleToUser(userId, roleName, version);
        logger.info("assignRealmRole returning: {}", response);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}/{roleName}")
    public ResponseEntity<UserDTO> removeRoleFromUser(@PathVariable Long userId, @PathVariable String roleName,
                                                      @RequestHeader(name = HttpHeaders.IF_MATCH, required = true) String ifMatch) {
        logger.info("Calling removeRealmRole with userId: {}, roleName: {}, ifMatch: {}", userId, roleName, ifMatch);
        int version = EtagUtils.parseIfMatchToVersion(ifMatch);
        UserDTO response = roleService.removeRoleFromUser(userId, roleName, version);
        logger.info("removeRealmRole returning: {}", response);
        return ResponseEntity.ok(response);
    }
}
