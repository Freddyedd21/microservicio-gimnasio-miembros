package com.analisys.gimnasio.miembros_service.dto;

import lombok.Data;

@Data
public class DatosEntrenamientoRequest {
    private String tipoEntrenamiento;
    private int duracionMinutos;
    private int calorias;
}
