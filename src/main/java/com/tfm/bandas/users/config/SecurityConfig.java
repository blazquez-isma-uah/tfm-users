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
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

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
        var matcher = PathPatternRequestMatcher.withDefaults();

        http.csrf(AbstractHttpConfigurer::disable);

        // CORS: activo en local (gestionado por Spring).
        // En AWS lo gestiona API Gateway - se desactiva en Spring.
        if (corsConfigurationSource != null) {
            http.cors(cors -> cors.configurationSource(corsConfigurationSource));
        } else {
            http.cors(AbstractHttpConfigurer::disable);
        }

        http.authorizeHttpRequests(auth -> auth
                        // Endpoints publicos: actuator health y Swagger (solo en local)
                        // PathPatternRequestMatcher evita MvcRequestMatcher, que en el contenedor
                        // serverless de Lambda causa NullPointerException al llamar a
                        // ServletRegistration.getMappings() (null en contexto serverless).
                        .requestMatchers(
                                matcher.matcher("/actuator/health"),
                                matcher.matcher("/swagger-ui.html"),
                                matcher.matcher("/swagger-ui/**"),
                                matcher.matcher("/v3/api-docs/**")
                        ).permitAll()
                        .requestMatchers(
                                matcher.matcher(HttpMethod.GET, "/api/users/**"),
                                matcher.matcher(HttpMethod.GET, "/api/roles/**"),
                                matcher.matcher(HttpMethod.GET, "/api/instruments/**")
                        ).hasAnyRole("ADMIN", "MUSICIAN")
                        .requestMatchers(
                                matcher.matcher(HttpMethod.PUT, "/api/users/me/**"),
                                matcher.matcher(HttpMethod.DELETE, "/api/users/me/**")
                        ).authenticated()
                        .requestMatchers(
                                matcher.matcher(HttpMethod.POST, "/api/users/picture/me/**"),
                                matcher.matcher(HttpMethod.PUT, "/api/users/picture/me/**")
                        ).authenticated()
                        .requestMatchers(
                                matcher.matcher(HttpMethod.POST, "/api/users/**"),
                                matcher.matcher(HttpMethod.POST, "/api/roles/**"),
                                matcher.matcher(HttpMethod.POST, "/api/instruments/**")
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                matcher.matcher(HttpMethod.PUT, "/api/users/**"),
                                matcher.matcher(HttpMethod.PUT, "/api/roles/**"),
                                matcher.matcher(HttpMethod.PUT, "/api/instruments/**")
                        ).hasRole("ADMIN")
                        .requestMatchers(
                                matcher.matcher(HttpMethod.DELETE, "/api/users/**"),
                                matcher.matcher(HttpMethod.DELETE, "/api/roles/**"),
                                matcher.matcher(HttpMethod.DELETE, "/api/instruments/**")
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(conv)));

        return http.build();
    }
}