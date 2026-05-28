package com.tfm.bandas.users.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Convierte los claims de roles/grupos del JWT en GrantedAuthority de Spring Security.
 * <p>
 * Soporta dos proveedores de identidad de forma transparente:
 *   - Amazon Cognito (perfil aws):  claim "cognito:groups"  → ["ADMIN", "MUSICIAN"]
 *   - Keycloak       (perfil local): claim "realm_access.roles" → ["ADMIN", "MUSICIAN"]
 * <p>
 * La lógica comprueba Cognito primero. Si no encuentra el claim, cae en Keycloak.
 * Esto elimina la necesidad de dos implementaciones con @Profile distintos,
 * ya que en cada entorno solo uno de los dos claims estará presente en el JWT.
 */
@Component
public class JwtAuthConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // ── Cognito (AWS) ─────────────────────────────────────────────────
        // Cognito incluye cognito:groups automáticamente en el Access Token
        // cuando el usuario pertenece a grupos. No requiere configuración adicional.
        List<String> cognitoGroups = jwt.getClaimAsStringList("cognito:groups");
        if (cognitoGroups != null && !cognitoGroups.isEmpty()) {
            return cognitoGroups.stream()
                    .filter(Objects::nonNull)
                    .map(g -> new SimpleGrantedAuthority("ROLE_" + g.toUpperCase()))
                    .collect(Collectors.toSet());
        }

        // ── Keycloak (local) ──────────────────────────────────────────────
        // Keycloak publica los roles del realm en realm_access.roles.
        // Este bloque solo se ejecuta si cognito:groups no está presente,
        // lo que ocurre únicamente cuando el token viene de Keycloak.
        var realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.get("roles") instanceof List<?> roles) {
            return roles.stream()
                    .filter(Objects::nonNull)
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toString().toUpperCase()))
                    .collect(Collectors.toSet());
        }

        return Set.of();
    }
}