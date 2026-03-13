package com.tfm.bandas.users.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException ex) {
        return Map.of("error", "Not Found", "message", ex.getMessage());
    }

    @ExceptionHandler({ BadRequestException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleBadRequest(RuntimeException ex) {
        return Map.of("error", "Bad Request", "message", ex.getMessage());
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
        return Map.of("error", "Validation Failed", "details", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleNotReadable(HttpMessageNotReadableException ex) {
        // devolver el mensaje principal para evitar dependencias en la causa específica
        return Map.of("error", "Malformed JSON", "message", ex.getMessage());
    }

    @ExceptionHandler({ AccessDeniedException.class })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleDenied(AccessDeniedException ex) {
        return Map.of("error", "Forbidden", "message", ex.getMessage() != null ? ex.getMessage() : "Insufficient permissions");
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeign(FeignException ex) {
        HttpStatus status;
        try {
            status = HttpStatus.valueOf(ex.status());
        } catch (Exception e) {
            status = HttpStatus.BAD_GATEWAY;
        }
        String msg = ex.contentUTF8();
        return ResponseEntity.status(status)
                .body(Map.of("error", "Upstream Error", "status", status.value(), "message", msg));
    }


    @ExceptionHandler(PreconditionRequiredException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_REQUIRED)
    public Map<String, Object> handlePreconditionRequired(PreconditionRequiredException ex) {
        return Map.of("error", "Precondition Required", "message", ex.getMessage());
    }

    @ExceptionHandler(PreconditionFailedException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public Map<String, Object> handlePreconditionFailed(PreconditionFailedException ex) {
        return Map.of("error", "Precondition Failed", "message", ex.getMessage());
    }


}
