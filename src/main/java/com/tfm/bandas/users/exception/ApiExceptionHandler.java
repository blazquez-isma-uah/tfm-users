package com.tfm.bandas.users.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException ex) {
        return Map.of("error", "No Encontrado", "message", ex.getMessage());
    }

    @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        return Map.of("error", "Petición Inválida", "message", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fe -> {
                            String msg = fe.getDefaultMessage();
                            return msg == null ? "" : msg;
                        },
                        (a,b) -> a, LinkedHashMap::new));
        return Map.of("error", "Errores de Validación", "message", "Uno o más campos no son válidos.", "details", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Malformed request body: {}", ex.getMessage());
        return Map.of("error", "JSON Malformado", "message", "El cuerpo de la petición no es un JSON válido o no tiene el formato esperado.");
    }

    @ExceptionHandler({ AccessDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleDenied(AccessDeniedException ex) {
        return Map.of("error", "Acceso Denegado", "message", "No tienes permisos para realizar esta operación.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.error("Data integrity violation", ex);
        return Map.of("error", "Conflicto de Datos",
                "message", "No se ha podido completar la operación porque los datos entran en conflicto con información ya existente.");
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeign(FeignException ex) {
        HttpStatus status;
        try {
            status = HttpStatus.valueOf(ex.status());
        } catch (Exception e) {
            status = HttpStatus.BAD_GATEWAY;
        }
        logger.error("Error del servicio de identidad (status {}): {}", ex.status(), ex.getMessage(), ex);

        // Intentamos leer el cuerpo estructurado que ahora devuelve MS Identity
        // ({"error", "errorCode", "message"}). Si Identity no devolviera ese
        // formato (versión antigua, error de red, etc.), caemos al mapeo
        // genérico por status que ya teníamos.
        String errorCode = null;
        try {
            Map<?, ?> identityBody = objectMapper.readValue(ex.contentUTF8(), Map.class);
            Object code = identityBody.get("errorCode");
            if (code != null) errorCode = code.toString();
        } catch (Exception parseEx) {
            // Cuerpo no parseable como JSON: seguimos con el fallback genérico.
        }

        if ("INVALID_PASSWORD".equals(errorCode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "error", "Petición Inválida", "errorCode", errorCode,
                    "message", "La contraseña no cumple los requisitos de seguridad: mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número."));
        }
        if ("USERNAME_EXISTS".equals(errorCode)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "Conflicto de Datos", "errorCode", errorCode,
                    "message", "Ya existe un usuario con ese nombre de usuario o email."));
        }

        String msg = switch (status) {
            case NOT_FOUND -> "El recurso solicitado no existe en el servicio de identidad.";
            case BAD_REQUEST -> "Los datos enviados no son válidos según el servicio de identidad.";
            case UNAUTHORIZED, FORBIDDEN -> "No tienes permisos para realizar esta operación en el servicio de identidad.";
            case CONFLICT -> "Ya existe un conflicto con los datos proporcionados en el servicio de identidad.";
            default -> "El servicio de identidad no está disponible en este momento. Inténtalo de nuevo más tarde.";
        };
        return ResponseEntity.status(status)
                .body(Map.of("error", "Error del Servicio de Identidad", "status", status.value(), "message", msg));
    }

    @ExceptionHandler(PreconditionRequiredException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    public Map<String, Object> handlePreconditionRequired(PreconditionRequiredException ex) {
        return Map.of("error", "Precondición Requerida", "message", ex.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    public Map<String, Object> handleMissingHeader(MissingRequestHeaderException ex) {
        return Map.of("error", "Precondición Requerida", "message", "La cabecera If-Match es obligatoria.");
    }

    @ExceptionHandler(PreconditionFailedException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public Map<String, Object> handlePreconditionFailed(PreconditionFailedException ex) {
        return Map.of("error", "Precondición Fallida", "message", ex.getMessage());
    }

    @ExceptionHandler(FeatureNotAvailableException.class)
    @ResponseStatus(HttpStatus.NOT_IMPLEMENTED)
    public Map<String, Object> handleFeatureNotAvailable(FeatureNotAvailableException ex) {
        return Map.of("error", "Funcionalidad No Disponible", "message", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleUnexpected(Exception ex) {
        logger.error("Unhandled exception", ex);
        return Map.of("error", "Error Interno", "message", "Ha ocurrido un error inesperado. Inténtalo de nuevo más tarde.");
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, Object> handleConflict(ConflictException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Conflicto de Datos");
        if (ex.getErrorCode() != null) body.put("errorCode", ex.getErrorCode());
        body.put("message", ex.getMessage());
        return body;
    }

}
