package com.analisys.gimnasio.miembros_service.controller;

import com.analisys.gimnasio.miembros_service.dto.AsignarEntrenadorRequest;
import com.analisys.gimnasio.miembros_service.dto.MiembroConEntrenadorDTO;
import com.analisys.gimnasio.miembros_service.dto.TrainerResponseDTO;
import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.service.MiembroTrainerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para la integración Miembros - Entrenadores
 */
@RestController
@RequestMapping("/api/miembros")
public class MiembroTrainerController {

    private final MiembroTrainerService miembroTrainerService;

    public MiembroTrainerController(MiembroTrainerService miembroTrainerService) {
        this.miembroTrainerService = miembroTrainerService;
    }

    /**
     * Asigna un entrenador personal a un miembro
     * POST /api/miembros/{id}/entrenador-personal
     */
    @PostMapping("/{miembroId}/entrenador-personal")
    public ResponseEntity<MiembroConEntrenadorDTO> asignarEntrenadorPersonal(
            @PathVariable Long miembroId,
            @RequestBody AsignarEntrenadorRequest request) {
        MiembroConEntrenadorDTO resultado = miembroTrainerService.asignarEntrenadorPersonal(miembroId, request.getEntrenadorId());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene el detalle de un miembro con info del entrenador personal
     * GET /api/miembros/{id}/con-entrenador
     */
    @GetMapping("/{miembroId}/con-entrenador")
    public ResponseEntity<MiembroConEntrenadorDTO> obtenerMiembroConEntrenador(@PathVariable Long miembroId) {
        MiembroConEntrenadorDTO resultado = miembroTrainerService.obtenerMiembroConEntrenador(miembroId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Lista todos los miembros con información de entrenadores personales
     * GET /api/miembros/con-entrenadores
     */
    @GetMapping("/con-entrenadores")
    public ResponseEntity<List<MiembroConEntrenadorDTO>> listarMiembrosConEntrenadores() {
        List<MiembroConEntrenadorDTO> miembros = miembroTrainerService.listarMiembrosConEntrenadores();
        return ResponseEntity.ok(miembros);
    }

    /**
     * Lista miembros asignados a un entrenador específico
     * GET /api/miembros/por-entrenador/{entrenadorId}
     */
    @GetMapping("/por-entrenador/{entrenadorId}")
    public ResponseEntity<List<MiembroConEntrenadorDTO>> obtenerMiembrosPorEntrenador(
            @PathVariable Long entrenadorId) {
        List<MiembroConEntrenadorDTO> miembros = miembroTrainerService.obtenerMiembrosPorEntrenador(entrenadorId);
        return ResponseEntity.ok(miembros);
    }

    /**
     * Busca entrenadores por especialidad
     * GET /api/miembros/entrenadores/por-especialidad?especialidad=Funcional
     */
    @GetMapping("/entrenadores/por-especialidad")
    public ResponseEntity<List<TrainerResponseDTO>> buscarEntrenadoresPorEspecialidad(
            @RequestParam String especialidad) {
        List<TrainerResponseDTO> entrenadores = miembroTrainerService.buscarEntrenadoresPorEspecialidad(especialidad);
        return ResponseEntity.ok(entrenadores);
    }

    /**
     * Remueve el entrenador personal de un miembro
     * DELETE /api/miembros/{id}/entrenador-personal
     */
    @DeleteMapping("/{miembroId}/entrenador-personal")
    public ResponseEntity<Miembro> removerEntrenadorPersonal(@PathVariable Long miembroId) {
        Miembro miembro = miembroTrainerService.removerEntrenadorPersonal(miembroId);
        return ResponseEntity.ok(miembro);
    }

    /**
     * Lista todos los entrenadores disponibles (desde servicio de entrenadores)
     * GET /api/miembros/entrenadores-disponibles
     */
    @GetMapping("/entrenadores-disponibles")
    public ResponseEntity<List<TrainerResponseDTO>> listarEntrenadoresDisponibles() {
        List<TrainerResponseDTO> entrenadores = miembroTrainerService.listarEntrenadoresDisponibles();
        return ResponseEntity.ok(entrenadores);
    }
}
