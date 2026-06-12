package com.tfm.bandas.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración CORS activa únicamente en el perfil local (Docker Compose).
 * <p>
 * En AWS, el CORS lo gestiona API Gateway antes de que la petición llegue
 * a la Lambda. Spring no necesita (ni debe) configurar CORS en ese entorno.
 * Al estar anotado con @Profile("docker"), este bean no existe en el contexto
 * de Spring cuando SPRING_PROFILES_ACTIVE=aws, por lo que SecurityConfig
 * desactiva CORS en Spring automáticamente.
 */
@Configuration
@Profile("docker")
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:5173"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "If-Match"));
        cfg.setExposedHeaders(List.of("ETag"));
        cfg.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}