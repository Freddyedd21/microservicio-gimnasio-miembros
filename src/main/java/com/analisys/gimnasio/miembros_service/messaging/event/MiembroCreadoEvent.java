package com.analisys.gimnasio.miembros_service.messaging.event;

import java.time.Instant;
import java.time.LocalDate;

public record MiembroCreadoEvent(
        Long miembroId,
        String nombre,
        String email,
        LocalDate fechaInscripcion,
        Long entrenadorPersonalId,
        Instant occurredAt
) {
}
