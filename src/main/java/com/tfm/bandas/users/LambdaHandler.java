package com.tfm.bandas.users;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.model.HttpApiV2ProxyRequest;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * Punto de entrada de AWS Lambda para MS Usuarios.
 * En AWS Lambda, el runtime no ejecuta 'java -jar'.
 * En su lugar, invoca a handleRequest() por cada petición HTTP que llega desde API Gateway.
 * <p>
 * La librería aws-serverless-java-container traduce el evento HttpApiV2ProxyRequest
 * (formato de API Gateway HTTP API v2) al formato HttpServletRequest que
 * Spring MVC entiende, y convierte la respuesta de vuelta al formato que Lambda devuelve a API Gateway.
 * <p>
 * HttpApiV2ProxyRequest corresponde al formato de payload v2 de API Gateway HTTP API
 */
public class LambdaHandler implements RequestHandler<HttpApiV2ProxyRequest, AwsProxyResponse> {

    private static final SpringBootLambdaContainerHandler<HttpApiV2ProxyRequest, AwsProxyResponse> handler;

    /*
     * El bloque estático se ejecuta una sola vez cuando Lambda carga la clase por primera vez
     * SpringBootLambdaContainerHandler arranca el contexto completo de Spring Boot en ese momento
     * Las invocaciones siguientes reutilizan el handler ya inicializado — no rearrancan Spring
     * Esto hace que el tiempo de arranque de la primera invocación sea alto (cold start),
     * pero las siguientes sean mucho más rápidas (SnapStart o warm invocations).
     */
    static {
        try {
            handler = SpringBootLambdaContainerHandler.getHttpApiV2ProxyHandler(UsuariosApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("No se pudo inicializar el contenedor Spring Boot en Lambda", e);
        }
    }

    @Override
    public AwsProxyResponse handleRequest(HttpApiV2ProxyRequest input, Context context) {
        return handler.proxy(input, context);
    }
}
