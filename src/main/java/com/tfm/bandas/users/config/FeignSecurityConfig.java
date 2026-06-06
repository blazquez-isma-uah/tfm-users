package com.tfm.bandas.users.config;

import feign.codec.Decoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
  @Bean
  public Decoder feignDecoder() {
    // Jackson converter que acepta tanto application/json como text/plain.
    // Necesario porque spring-cloud-function-serverless-web establece
    // Content-Type: text/plain en las respuestas Lambda aunque el body sea JSON valido.
    // Sin esto, Feign no puede deserializar la respuesta.
    MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
    List<MediaType> supportedMediaTypes = new ArrayList<>(jacksonConverter.getSupportedMediaTypes());
    supportedMediaTypes.add(MediaType.TEXT_PLAIN);
    jacksonConverter.setSupportedMediaTypes(supportedMediaTypes);

    return new SpringDecoder(
            () -> new HttpMessageConverters(false, Collections.singletonList(jacksonConverter))
    );
  }

}
