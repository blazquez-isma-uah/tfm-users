package com.tfm.bandas.users.utils;

public class Constants {
    public static final String[] PATTERNS_PERMITED = {"/actuator/health", "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"};
    public static final String[] PATTERNS_AUTHENTICATED = {"/api/users/**", "/api/roles/**", "/api/instruments/**"};

    public static final String PATH_USERS = "/api/identity/users";
    public static final String PATH_ROLES = "/api/identity/roles";
}
