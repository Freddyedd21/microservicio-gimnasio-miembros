package com.analisys.gimnasio.miembros_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroEquipmentResponse {
    
    private String mensaje;
    private String miembroNombre;
    private String equipoNombre;
    private Integer cantidadUsada;
    private Integer cantidadDisponibleRestante;
}
