package com.analisys.gimnasio.miembros_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO con información completa del miembro incluyendo entrenador personal
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroConEntrenadorDTO {
    private Long id;
    private String nombre;
    private String email;
    private LocalDate fechaInscripcion;
    private Long entrenadorPersonalId;
    private TrainerResponseDTO entrenadorPersonal;
}
