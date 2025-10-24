package com.tfm.bandas.usuarios.client;

import com.tfm.bandas.usuarios.dto.KeycloakUserRegisterRequest;
import com.tfm.bandas.usuarios.dto.KeycloakUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "identityClient",
        url = "${identity.service.uri}"
)
public interface IdentityClient {

    @PostMapping("/api/identity/keycloak/users")
    KeycloakUserResponse createUserInKeycloak(@RequestBody KeycloakUserRegisterRequest dto);

    @DeleteMapping("/api/identity/keycloak/users/{keycloakId}")
    void deleteUserInKeycloak(@PathVariable("keycloakId") String keycloakId);
}
