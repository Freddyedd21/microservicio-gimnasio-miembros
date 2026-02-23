package com.analisys.gimnasio.miembros_service.exception;

/**
 * Excepción lanzada cuando hay problemas al comunicarse con el servicio de equipos.
 */
public class EquipmentServiceException extends RuntimeException {
    
    public EquipmentServiceException(String message) {
        super(message);
    }
    
    public EquipmentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
