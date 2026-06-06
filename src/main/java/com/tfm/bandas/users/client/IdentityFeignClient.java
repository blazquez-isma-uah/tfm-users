package com.tfm.bandas.users.client;

import com.tfm.bandas.users.config.FeignSecurityConfig;
import com.tfm.bandas.users.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.tfm.bandas.users.utils.Constants.PATH_USERS;
import static com.tfm.bandas.users.utils.Constants.PATH_ROLES;

/**
 * Cliente Feign hacia MS Identity.
 * Las rutas están alineadas con los controladores de MS Identity:
 * - /api/identity/users  (UserController)
 * - /api/identity/roles  (RoleController)
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

    @PostMapping(value = PATH_USERS, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdentityUserResponse createUser(@RequestBody IdentityUserRegisterRequest dto);

    @GetMapping(PATH_USERS + "/{userId}")
    IdentityUserResponse getUserById(@PathVariable("userId") String userId);

    @GetMapping(PATH_USERS + "/{userId}/details")
    IdentityUserDetailsResponse getUserDetailsById(@PathVariable("userId") String userId);

    @GetMapping(PATH_USERS + "/username/{username}")
    IdentityUserResponse getUserByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS + "/username/{username}/details")
    IdentityUserDetailsResponse getUserDetailsByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS)
    List<IdentityUserResponse> listAllUsers();

    @DeleteMapping(PATH_USERS + "/{userId}")
    void deleteUserById(@PathVariable("userId") String userId);

    @DeleteMapping(PATH_USERS + "/username/{username}")
    void deleteUserByUsername(@PathVariable("username") String username);

    @PutMapping(value = PATH_USERS + "/{userId}/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    void updateUserPassword(
            @PathVariable("userId") String userId,
            @RequestBody IdentityUserPasswordUpdateRequest dto);

    @PutMapping(value = PATH_USERS + "/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    IdentityUserResponse updateUserData(
            @PathVariable("userId") String userId,
            @RequestBody IdentityUserUpdateRequest dto);

    @GetMapping(PATH_USERS + "/exists/username/{username}")
    Boolean userExistsByUsername(@PathVariable("username") String username);

    @GetMapping(PATH_USERS + "/exists/email/{email}")
    Boolean userExistsByEmail(@PathVariable("email") String email);


    // =========================
    // ROLES (RoleController)
    // =========================

    @GetMapping(PATH_ROLES)
    List<IdentityRoleResponse> listAllRoles();

    @PostMapping(value = PATH_ROLES, consumes = MediaType.APPLICATION_JSON_VALUE)
    IdentityRoleResponse createRole(@RequestBody IdentityRoleRegisterRequest dto);

    @GetMapping(PATH_ROLES + "/{roleId}")
    IdentityRoleResponse getRoleById(@PathVariable("roleId") String roleId);

    @GetMapping(PATH_ROLES + "/name/{roleName}")
    IdentityRoleResponse getRoleByName(@PathVariable("roleName") String roleName);

    @DeleteMapping(PATH_ROLES + "/{roleName}")
    void deleteRole(@PathVariable("roleName") String roleName);

    @GetMapping(PATH_ROLES + "/user/{userId}")
    List<IdentityRoleResponse> listUserRoles(@PathVariable("userId") String userId);

    @PostMapping(PATH_ROLES + "/user/{userId}/{roleName}")
    void assignRoleToUser(
            @PathVariable("userId") String userId,
            @PathVariable("roleName") String roleName);

    @DeleteMapping(PATH_ROLES + "/user/{userId}/{roleName}")
    void removeRoleFromUser(
            @PathVariable("userId") String userId,
            @PathVariable("roleName") String roleName);
}
