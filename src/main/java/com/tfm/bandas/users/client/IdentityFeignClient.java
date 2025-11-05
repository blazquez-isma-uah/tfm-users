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

    @GetMapping(PATH_USERS + "/{id}")
    KeycloakUserResponse getUserById(@PathVariable("id") String id);

    @GetMapping(PATH_USERS + "/{id}/details")
    KeycloakUserDetailsResponse getUserDetailsById(@PathVariable("id") String id);

    @GetMapping(PATH_USERS + "/username/{username}")
    KeycloakUserResponse getUserByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS + "/username/{username}/details")
    KeycloakUserDetailsResponse getUserDetailsByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS)
    List<KeycloakUserResponse> listAllUsers();

    @DeleteMapping(PATH_USERS + "/{id}")
    void deleteUserByIamId(@PathVariable("id") String id);

    @DeleteMapping(PATH_USERS + "/username/{username}")
    void deleteUserByUsername(@PathVariable("username") String username);

    @PutMapping(PATH_USERS + "/{id}/password")
    void updateUserPassword(@PathVariable("id") String id, @RequestBody KeycloakUserPasswordUpdateRequest dto);

    @PutMapping(PATH_USERS + "/{id}")
    KeycloakUserResponse updateUserData(@PathVariable("id") String id, @RequestBody KeycloakUserUpdateRequest dto);

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

    @GetMapping(PATH_ROLES + "/{id}")
    KeycloakRoleResponse getRoleById(@PathVariable("id") String id);

    @GetMapping(PATH_ROLES + "/name/{name}")
    KeycloakRoleResponse getRoleByName(@PathVariable("name") String name);

    @DeleteMapping(PATH_ROLES + "/{role}")
    void deleteRealmRole(@PathVariable("role") String role);

    @GetMapping(PATH_ROLES + "/user/{id}")
    List<KeycloakRoleResponse> listUserRoles(@PathVariable("id") String userId);

    @PostMapping(PATH_ROLES + "/user/{id}/{roleName}")
    void assignRoleToUser(@PathVariable("id") String userId, @PathVariable("roleName") String roleName);

    @DeleteMapping(PATH_ROLES + "/user/{id}/{roleName}")
    void removeRoleFromUser(@PathVariable("id") String userId, @PathVariable("roleName") String roleName);
}
