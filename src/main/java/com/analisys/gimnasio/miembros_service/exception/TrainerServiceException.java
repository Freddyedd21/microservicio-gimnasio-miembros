package com.analisys.gimnasio.miembros_service.exception;

public class TrainerServiceException extends RuntimeException {
    public TrainerServiceException(String message) {
        super(message);
    }

    public TrainerServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
