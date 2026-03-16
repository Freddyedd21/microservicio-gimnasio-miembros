package com.analisys.gimnasio.miembros_service.stream;

import java.time.LocalDateTime;

import com.analisys.gimnasio.miembros_service.kafka.DatosEntrenamiento;

import lombok.Data;

@Data
public class ResumenEntrenamiento {
    private int totalSesiones;
    private int totalMinutos;
    private int totalCalorias;
    private LocalDateTime ultimaActualizacion;

    public ResumenEntrenamiento actualizar(DatosEntrenamiento dato) {
        totalSesiones += 1;
        totalMinutos += dato.getDuracionMinutos();
        totalCalorias += dato.getCalorias();
        ultimaActualizacion = LocalDateTime.now();
        return this;
    }
}
