package com.tfm.bandas.usuarios.config;

import com.tfm.bandas.usuarios.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static com.tfm.bandas.usuarios.utils.Constants.PATTERNS_AUTHENTICATED;
import static com.tfm.bandas.usuarios.utils.Constants.PATTERNS_PERMITED;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(SecurityConfig::extractRealmRoles);

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsCfg()))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(PATTERNS_PERMITED).permitAll()
                    // Ajusta según tu API:
                    .requestMatchers(HttpMethod.GET, PATTERNS_AUTHENTICATED).hasAnyRole("ADMIN","MUSICIAN")
                    .requestMatchers(HttpMethod.POST, PATTERNS_AUTHENTICATED).hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, PATTERNS_AUTHENTICATED).hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, PATTERNS_AUTHENTICATED).hasRole("ADMIN")
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conv)));

        return http.build();
    }

    /**
     * Extrae los roles del realm del token JWT de Keycloak (realm_access.roles) y los convierte
     * en una colección de GrantedAuthority con el prefijo "ROLE_". Esto permite que Spring Security
     * reconozca y utilice estos roles para la autorización basada en roles.
     * @param jwt
     * @return
     */
    private static Collection<GrantedAuthority> extractRealmRoles(Jwt jwt) {
        var out = new HashSet<SimpleGrantedAuthority>();
        var realm = jwt.getClaimAsMap(Constants.REALM_ACCESS);
        if (realm != null && realm.get(Constants.ROLES) instanceof List<?> roles) {
            for (Object r : roles) out.add(new SimpleGrantedAuthority("ROLE_" + r.toString()));
        }
        return new HashSet<>(out);
    }

    @Bean
    CorsConfigurationSource corsCfg() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type"));
        cfg.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
