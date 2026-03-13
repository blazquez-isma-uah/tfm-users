package com.tfm.bandas.users.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;

// Configuración para que los clientes Feign propaguen el token OAuth2 JWT de la solicitud entrante a las solicitudes salientes.

@Configuration
public class FeignSecurityConfig {

  @Bean
  public feign.RequestInterceptor oauth2FeignRequestInterceptor() {
    return template -> {
      var auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth instanceof org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth) {
        String token = jwtAuth.getToken().getTokenValue();
        template.header(org.springframework.http.HttpHeaders.AUTHORIZATION, "Bearer " + token);
      }
    };
  }
}
