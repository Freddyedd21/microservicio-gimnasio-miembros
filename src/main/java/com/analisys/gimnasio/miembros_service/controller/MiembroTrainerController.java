package com.analisys.gimnasio.miembros_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.analisys.gimnasio.miembros_service.dto.AsignarEntrenadorRequest;
import com.analisys.gimnasio.miembros_service.dto.ErrorResponse;
import com.analisys.gimnasio.miembros_service.dto.MiembroConEntrenadorDTO;
import com.analisys.gimnasio.miembros_service.dto.TrainerResponseDTO;
import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.service.MiembroTrainerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller para la integración Miembros - Entrenadores
 */
@RestController
@RequestMapping("/api/miembros")
@Tag(name = "Miembros - Entrenadores", description = "Integración de miembros con gym-entrenador-service")
@SecurityRequirement(name = "bearer-jwt")
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
        @Operation(summary = "Asignar entrenador personal",
            description = "Asigna un entrenador personal a un miembro (valida contra el servicio de entrenadores)")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrenador asignado",
            content = @Content(schema = @Schema(implementation = MiembroConEntrenadorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "trainer-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<MiembroConEntrenadorDTO> asignarEntrenadorPersonal(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long miembroId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "ID del entrenador a asignar",
                required = true,
                content = @Content(schema = @Schema(implementation = AsignarEntrenadorRequest.class)))
            @RequestBody AsignarEntrenadorRequest request) {
        MiembroConEntrenadorDTO resultado = miembroTrainerService.asignarEntrenadorPersonal(miembroId, request.getEntrenadorId());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene el detalle de un miembro con info del entrenador personal
     * GET /api/miembros/{id}/con-entrenador
     */
    @GetMapping("/{miembroId}/con-entrenador")
        @Operation(summary = "Obtener miembro con entrenador",
            description = "Obtiene el miembro y, si aplica, su entrenador personal")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detalle del miembro",
            content = @Content(schema = @Schema(implementation = MiembroConEntrenadorDTO.class))),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<MiembroConEntrenadorDTO> obtenerMiembroConEntrenador(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long miembroId) {
        MiembroConEntrenadorDTO resultado = miembroTrainerService.obtenerMiembroConEntrenador(miembroId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Lista todos los miembros con información de entrenadores personales
     * GET /api/miembros/con-entrenadores
     */
    @GetMapping("/con-entrenadores")
        @Operation(summary = "Listar miembros con entrenadores",
            description = "Lista miembros incluyendo información del entrenador personal cuando esté disponible")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de miembros",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MiembroConEntrenadorDTO.class)))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<List<MiembroConEntrenadorDTO>> listarMiembrosConEntrenadores() {
        List<MiembroConEntrenadorDTO> miembros = miembroTrainerService.listarMiembrosConEntrenadores();
        return ResponseEntity.ok(miembros);
    }

    /**
     * Lista miembros asignados a un entrenador específico
     * GET /api/miembros/por-entrenador/{entrenadorId}
     */
    @GetMapping("/por-entrenador/{entrenadorId}")
        @Operation(summary = "Listar miembros por entrenador",
            description = "Lista miembros asignados a un entrenador específico")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de miembros",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MiembroConEntrenadorDTO.class)))),
        @ApiResponse(responseCode = "503", description = "trainer-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<List<MiembroConEntrenadorDTO>> obtenerMiembrosPorEntrenador(
            @Parameter(description = "ID del entrenador", required = true, example = "5")
            @PathVariable Long entrenadorId) {
        List<MiembroConEntrenadorDTO> miembros = miembroTrainerService.obtenerMiembrosPorEntrenador(entrenadorId);
        return ResponseEntity.ok(miembros);
    }

    /**
     * Busca entrenadores por especialidad
     * GET /api/miembros/entrenadores/por-especialidad?especialidad=Funcional
     */
    @GetMapping("/entrenadores/por-especialidad")
        @Operation(summary = "Buscar entrenadores por especialidad",
            description = "Consulta entrenadores por especialidad en el servicio de entrenadores")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de entrenadores",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerResponseDTO.class)))),
        @ApiResponse(responseCode = "503", description = "trainer-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<List<TrainerResponseDTO>> buscarEntrenadoresPorEspecialidad(
            @Parameter(description = "Especialidad a buscar", required = true, example = "Funcional")
            @RequestParam String especialidad) {
        List<TrainerResponseDTO> entrenadores = miembroTrainerService.buscarEntrenadoresPorEspecialidad(especialidad);
        return ResponseEntity.ok(entrenadores);
    }

    /**
     * Remueve el entrenador personal de un miembro
     * DELETE /api/miembros/{id}/entrenador-personal
     */
    @DeleteMapping("/{miembroId}/entrenador-personal")
        @Operation(summary = "Remover entrenador personal",
            description = "Quita el entrenador personal asignado a un miembro")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Entrenador removido",
            content = @Content(schema = @Schema(implementation = Miembro.class))),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<Miembro> removerEntrenadorPersonal(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long miembroId) {
        Miembro miembro = miembroTrainerService.removerEntrenadorPersonal(miembroId);
        return ResponseEntity.ok(miembro);
    }

    /**
     * Lista todos los entrenadores disponibles (desde servicio de entrenadores)
     * GET /api/miembros/entrenadores-disponibles
     */
    @GetMapping("/entrenadores-disponibles")
        @Operation(summary = "Listar entrenadores disponibles",
            description = "Obtiene todos los entrenadores disponibles desde el servicio de entrenadores")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de entrenadores",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TrainerResponseDTO.class)))),
        @ApiResponse(responseCode = "503", description = "trainer-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<List<TrainerResponseDTO>> listarEntrenadoresDisponibles() {
        List<TrainerResponseDTO> entrenadores = miembroTrainerService.listarEntrenadoresDisponibles();
        return ResponseEntity.ok(entrenadores);
    }
}
