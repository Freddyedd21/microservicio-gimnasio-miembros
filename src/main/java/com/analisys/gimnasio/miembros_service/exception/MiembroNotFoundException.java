package com.analisys.gimnasio.miembros_service.exception;

/**
 * Excepción lanzada cuando un miembro no es encontrado.
 */
public class MiembroNotFoundException extends RuntimeException {
    
    public MiembroNotFoundException(Long id) {
        super("Miembro no encontrado con ID: " + id);
    }
    
    public MiembroNotFoundException(String message) {
        super(message);
    }
}
