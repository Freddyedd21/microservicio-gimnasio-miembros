package com.analisys.gimnasio.miembros_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroUseEquipmentRequest {
    
    private Long miembroId;
    private Long equipmentId;
    private Integer cantidad;
}
