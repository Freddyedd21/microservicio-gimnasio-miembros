package com.analisys.gimnasio.miembros_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el servicio de miembros.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MiembroNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMiembroNotFound(MiembroNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(EquipmentServiceException.class)
    public ResponseEntity<Map<String, Object>> handleEquipmentServiceException(EquipmentServiceException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        error.put("error", "Service Unavailable");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(TrainerServiceException.class)
    public ResponseEntity<Map<String, Object>> handleTrainerServiceException(TrainerServiceException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        error.put("error", "Trainer Service Unavailable");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", "Ha ocurrido un error inesperado");
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
