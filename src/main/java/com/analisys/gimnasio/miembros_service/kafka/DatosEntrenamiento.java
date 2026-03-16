package com.analisys.gimnasio.miembros_service.kafka;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosEntrenamiento {
    private String miembroId;
    private String tipoEntrenamiento;
    private int duracionMinutos;
    private int calorias;
    private LocalDateTime timestamp;
}
