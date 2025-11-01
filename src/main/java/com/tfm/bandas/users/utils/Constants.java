package com.tfm.bandas.users.utils;

public class Constants {
    public static final String[] PATTERNS_PERMITED = {"/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"};
    public static final String[] PATTERNS_AUTHENTICATED = {"/api/users/**", "/api/roles/**", "/api/instruments/**"};

    public static final String URL_USERS = "/api/identity/keycloak/users";
    public static final String URL_ROLES = "/api/identity/keycloak/roles";
    public static final String REALM_ACCESS = "realm_access";
    public static final String ROLES = "roles";
}
