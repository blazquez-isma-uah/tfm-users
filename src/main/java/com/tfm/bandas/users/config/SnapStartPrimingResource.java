package com.tfm.bandas.users.config;

import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * Hook CRaC para SnapStart de AWS Lambda en el perfil aws.
 * <p>
 * Spring Boot 3.x + HikariCP gestionan automaticamente el cierre del pool
 * de conexiones en beforeCheckpoint y su reapertura en afterRestore cuando
 * org.crac esta en el classpath. No es necesario codigo adicional para eso.
 * <p>
 * Lo que si anadimos es una llamada de calentamiento a la BD en afterRestore:
 * fuerza la creacion de una conexion real antes de que llegue la primera
 * peticion del usuario, moviendo ese overhead (~500ms) al restore phase
 * en lugar de a la primera peticion visible al usuario.
 * <p>
 * En entornos sin CRaC (JVM estandar, perfil docker), org.crac proporciona
 * una implementacion no-op. Al estar anotado con @Profile("aws"), este
 * componente no se instancia en Docker Compose.
 */
@Component
@Profile("aws")
public class SnapStartPrimingResource implements Resource {

    @Autowired
    private DataSource dataSource;

    public SnapStartPrimingResource() {
        // Registra este componente como Resource CRaC en el contexto global.
        // Lambda invocara los hooks beforeCheckpoint y afterRestore.
        Core.getGlobalContext().register(this);
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
        // HikariCP cierra el pool automaticamente con CRaC antes del snapshot.
        // No se necesita codigo adicional aqui.
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        // Llamada de calentamiento: establece una conexion real con la BD
        // antes de que llegue la primera peticion del usuario.
        // HikariCP ya ha reabierto el pool en su propio hook afterRestore.
        // Esta llamada fuerza que el pool tenga al menos una conexion activa.
        try (Connection conn = dataSource.getConnection()) {
            conn.isValid(1);
        } catch (Exception e) {
            // Si el calentamiento falla no es critico: la primera peticion
            // establecera la conexion con un overhead minimo.
        }
    }
}