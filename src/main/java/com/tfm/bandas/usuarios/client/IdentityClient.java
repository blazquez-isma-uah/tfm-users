package com.tfm.bandas.usuarios.client;

import com.tfm.bandas.usuarios.config.FeignSecurityConfig;
import com.tfm.bandas.usuarios.dto.*; // <-- crea/ajusta estos DTOs espejo de identity
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tfm.bandas.usuarios.utils.Constants.URL_USERS;
import static com.tfm.bandas.usuarios.utils.Constants.URL_ROLES;

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
public interface IdentityClient {
    // =========================
    // USERS (UserController)
    // =========================

    @PostMapping(URL_USERS)
    KeycloakUserResponse createUserInKeycloak(@RequestBody KeycloakUserRegisterRequest dto);

    @GetMapping(URL_USERS + "/{id}")
    KeycloakUserResponse getUserById(@PathVariable("id") String id);

    @GetMapping(URL_USERS + "/{id}/details")
    KeycloakUserDetailsResponse getUserDetailsById(@PathVariable("id") String id);

    @GetMapping(URL_USERS + "/username/{username}")
    KeycloakUserResponse getUserByUsername(@PathVariable("username") String username);

    @GetMapping(URL_USERS + "/username/{username}/details")
    KeycloakUserDetailsResponse getUserDetailsByUsername(@PathVariable("username") String username);

    @GetMapping(URL_USERS)
    List<KeycloakUserResponse> listAllUsers();

    @DeleteMapping(URL_USERS + "/{id}")
    void deleteUserByIamId(@PathVariable("id") String id);

    @DeleteMapping(URL_USERS + "/username/{username}")
    void deleteUserByUsername(@PathVariable("username") String username);

    @PutMapping(URL_USERS + "/{id}/password")
    void updateUserPassword(@PathVariable("id") String id, @RequestBody KeycloakUserPasswordUpdateRequest dto);

    @PutMapping(URL_USERS + "/{id}")
    KeycloakUserResponse updateUserData(@PathVariable("id") String id, @RequestBody KeycloakUserUpdateRequest dto);

    @GetMapping(URL_USERS + "/exists/username/{username}")
    Boolean userExistsByUsername(@PathVariable("username") String username);

    @GetMapping(URL_USERS + "/exists/email/{email}")
    Boolean userExistsByEmail(@PathVariable("email") String email);


    // =========================
    // ROLES (RoleController)
    // =========================

    @GetMapping(URL_ROLES)
    List<KeycloakRoleResponse> listAllRoles();

    @PostMapping(URL_ROLES)
    KeycloakRoleResponse createRealmRole(@RequestBody KeycloakRoleRegisterRequest dto);

    @GetMapping(URL_ROLES + "/{id}")
    KeycloakRoleResponse getRoleById(@PathVariable("id") String id);

    @GetMapping(URL_ROLES + "/name/{name}")
    KeycloakRoleResponse getRoleByName(@PathVariable("name") String name);

    @DeleteMapping(URL_ROLES + "/{role}")
    void deleteRealmRole(@PathVariable("role") String role);

    @GetMapping(URL_ROLES + "/user/{id}")
    List<KeycloakRoleResponse> listUserRoles(@PathVariable("id") String userId);

    @PostMapping(URL_ROLES + "/user/{id}/{roleName}")
    void assignRealmRole(@PathVariable("id") String userId, @PathVariable("roleName") String roleName);

    @DeleteMapping(URL_ROLES + "/user/{id}/{roleName}")
    void removeRealmRole(@PathVariable("id") String userId, @PathVariable("roleName") String roleName);
}
