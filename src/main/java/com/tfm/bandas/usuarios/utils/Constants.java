package com.tfm.bandas.usuarios.utils;

import java.util.List;

public class Constants {

    public static final List<String> NOT_ALLOWED_PASSWORDS = List.of("null", "undefined");
    public static final String[] PATTERNS_PERMITED = {"/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"};

    public static final String URL_USERS = "/api/identity/keycloak/users";
    public static final String URL_ROLES = "/api/identity/keycloak/roles";
    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
}
