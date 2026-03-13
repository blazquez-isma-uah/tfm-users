package com.tfm.bandas.users.client;

import com.tfm.bandas.users.config.FeignSecurityConfig;
import com.tfm.bandas.users.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tfm.bandas.users.utils.Constants.PATH_USERS;
import static com.tfm.bandas.users.utils.Constants.PATH_ROLES;

/*
 * Cliente Feign hacia identity-service.
 * Las rutas/copias están alineadas con:
 * - /api/identity/keycloak/users  (UserController)
 * - /api/identity/keycloak/roles  (RoleController)
 */

@FeignClient(
        name = "identityClient",
        url = "${identity.service.uri}",
        configuration = FeignSecurityConfig.class
)
public interface IdentityFeignClient {
    // =========================
    // USERS (UserController)
    // =========================

    @PostMapping(PATH_USERS)
    KeycloakUserResponse createUserInKeycloak(@RequestBody KeycloakUserRegisterRequest dto);

    @GetMapping(PATH_USERS + "/{userId}")
    KeycloakUserResponse getUserById(@PathVariable("userId") String userId);

    @GetMapping(PATH_USERS + "/{userId}/details")
    KeycloakUserDetailsResponse getUserDetailsById(@PathVariable("userId") String userId);

    @GetMapping(PATH_USERS + "/username/{username}")
    KeycloakUserResponse getUserByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS + "/username/{username}/details")
    KeycloakUserDetailsResponse getUserDetailsByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS)
    List<KeycloakUserResponse> listAllUsers();

    @DeleteMapping(PATH_USERS + "/{userId}")
    void deleteUserByIamId(@PathVariable("userId") String userId);

    @DeleteMapping(PATH_USERS + "/username/{username}")
    void deleteUserByUsername(@PathVariable("username") String username);

    @PutMapping(PATH_USERS + "/{userId}/password")
    void updateUserPassword(@PathVariable("userId") String userId, @RequestBody KeycloakUserPasswordUpdateRequest dto);

    @PutMapping(PATH_USERS + "/{userId}")
    KeycloakUserResponse updateUserData(@PathVariable("userId") String userId, @RequestBody KeycloakUserUpdateRequest dto);

    @GetMapping(PATH_USERS + "/exists/username/{username}")
    Boolean userExistsByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS + "/exists/email/{email}")
    Boolean userExistsByEmail(@PathVariable("email") String email);


    // =========================
    // ROLES (RoleController)
    // =========================

    @GetMapping(PATH_ROLES)
    List<KeycloakRoleResponse> listAllRoles();

    @PostMapping(PATH_ROLES)
    KeycloakRoleResponse createRealmRole(@RequestBody KeycloakRoleRegisterRequest dto);

    @GetMapping(PATH_ROLES + "/{roleId}")
    KeycloakRoleResponse getRoleById(@PathVariable("roleId") String roleId);

    @GetMapping(PATH_ROLES + "/name/{roleName}")
    KeycloakRoleResponse getRoleByName(@PathVariable("roleName") String roleName);

    @DeleteMapping(PATH_ROLES + "/{roleName}")
    void deleteRealmRole(@PathVariable("roleName") String roleName);

    @GetMapping(PATH_ROLES + "/user/{userId}")
    List<KeycloakRoleResponse> listUserRoles(@PathVariable("userId") String userId);

    @PostMapping(PATH_ROLES + "/user/{userId}/{roleName}")
    void assignRoleToUser(@PathVariable("userId") String userId, @PathVariable("roleName") String roleName);

    @DeleteMapping(PATH_ROLES + "/user/{userId}/{roleName}")
    void removeRoleFromUser(@PathVariable("userId") String userId, @PathVariable("roleName") String roleName);
}
