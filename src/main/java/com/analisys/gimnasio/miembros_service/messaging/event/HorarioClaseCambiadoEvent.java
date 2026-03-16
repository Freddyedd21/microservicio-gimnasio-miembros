package com.analisys.gimnasio.miembros_service.messaging.event;

import java.time.Instant;
import java.time.LocalDateTime;

public class HorarioClaseCambiadoEvent {

    private Long claseId;
    private LocalDateTime horarioAnterior;
    private LocalDateTime horarioNuevo;
    private Instant occurredAt;

    public HorarioClaseCambiadoEvent() {
    }

    public Long getClaseId() {
        return claseId;
    }

    public LocalDateTime getHorarioAnterior() {
        return horarioAnterior;
    }

    public LocalDateTime getHorarioNuevo() {
        return horarioNuevo;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
