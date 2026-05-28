package com.tfm.bandas.users.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import static com.tfm.bandas.users.utils.Constants.*;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthConverter jwtAuthConverter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            JwtAuthConverter jwtAuthConverter,
            @Autowired(required = false) CorsConfigurationSource corsConfigurationSource) {
        this.jwtAuthConverter = jwtAuthConverter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        var conv = new JwtAuthenticationConverter();
        conv.setJwtGrantedAuthoritiesConverter(jwtAuthConverter);

        http.csrf(AbstractHttpConfigurer::disable);

        // CORS: activo en local (gestionado por Spring).
        // En AWS lo gestiona API Gateway — se desactiva en Spring.
        if (corsConfigurationSource != null) {
            http.cors(cors -> cors.configurationSource(corsConfigurationSource));
        } else {
            http.cors(AbstractHttpConfigurer::disable);
        }

        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(PATTERNS_PERMITED).permitAll()
                        .requestMatchers(HttpMethod.GET, PATTERNS_AUTHENTICATED).hasAnyRole("ADMIN", "MUSICIAN")
                        .requestMatchers(HttpMethod.PUT, "/api/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/me/**").authenticated()
                        .requestMatchers(HttpMethod.POST, PATTERNS_AUTHENTICATED).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, PATTERNS_AUTHENTICATED).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, PATTERNS_AUTHENTICATED).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conv)));

        return http.build();
    }
}