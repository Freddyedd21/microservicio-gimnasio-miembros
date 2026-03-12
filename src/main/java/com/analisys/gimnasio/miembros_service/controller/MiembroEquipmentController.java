package com.analisys.gimnasio.miembros_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.analisys.gimnasio.miembros_service.dto.EquipmentResponseDTO;
import com.analisys.gimnasio.miembros_service.dto.ErrorResponse;
import com.analisys.gimnasio.miembros_service.dto.MiembroEquipmentResponse;
import com.analisys.gimnasio.miembros_service.dto.MiembroUseEquipmentRequest;
import com.analisys.gimnasio.miembros_service.service.MiembroEquipmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para las operaciones de miembros con equipos.
 * endpoints para que los miembros puedan usar y liberar equipos.
 */
@RestController
@RequestMapping("/api/miembros")
@RequiredArgsConstructor
@Tag(name = "Miembros - Equipos", description = "Integración de miembros con equipment-service")
@SecurityRequirement(name = "bearer-jwt")
public class MiembroEquipmentController {

    private final MiembroEquipmentService miembroEquipmentService;

    /**
     * POST /api/miembros/{miembroId}/equipos/{equipoId}/usar
     * Permite a un miembro usar/reservar un equipo.
     */
    @PostMapping("/{miembroId}/equipos/{equipoId}/usar")
        @Operation(
            summary = "Usar/reservar equipo",
            description = "Reserva un equipo para un miembro (coordina con equipment-service)")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reserva realizada",
            content = @Content(schema = @Schema(implementation = MiembroEquipmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "equipment-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<MiembroEquipmentResponse> usarEquipo(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long miembroId,
            @Parameter(description = "ID del equipo", required = true, example = "10")
            @PathVariable Long equipoId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Cantidad a reservar",
                required = true,
                content = @Content(schema = @Schema(implementation = MiembroUseEquipmentRequest.class)))
            @RequestBody MiembroUseEquipmentRequest request) {
        
        MiembroEquipmentResponse response = miembroEquipmentService.usarEquipo(
                miembroId, 
                equipoId, 
                request.getCantidad()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/miembros/{miembroId}/equipos/{equipoId}/liberar
     * Permite a un miembro liberar/devolver un equipo.
     */
    @PostMapping("/{miembroId}/equipos/{equipoId}/liberar")
        @Operation(
            summary = "Liberar/devolver equipo",
            description = "Libera un equipo previamente reservado por un miembro (coordina con equipment-service)")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liberación realizada",
            content = @Content(schema = @Schema(implementation = MiembroEquipmentResponse.class))),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "equipment-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<MiembroEquipmentResponse> liberarEquipo(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long miembroId,
            @Parameter(description = "ID del equipo", required = true, example = "10")
            @PathVariable Long equipoId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Cantidad a liberar",
                required = true,
                content = @Content(schema = @Schema(implementation = MiembroUseEquipmentRequest.class)))
            @RequestBody MiembroUseEquipmentRequest request) {
        
        MiembroEquipmentResponse response = miembroEquipmentService.liberarEquipo(
                miembroId, 
                equipoId, 
                request.getCantidad()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/miembros/{miembroId}/equipos/disponibles
     * Obtiene todos los equipos disponibles para un miembro.
     */
    @GetMapping("/{miembroId}/equipos/disponibles")
        @Operation(
            summary = "Listar equipos disponibles",
            description = "Obtiene los equipos disponibles para usar (desde equipment-service)")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de equipos disponibles",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EquipmentResponseDTO.class)))),
        @ApiResponse(responseCode = "404", description = "Miembro no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "503", description = "equipment-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<List<EquipmentResponseDTO>> obtenerEquiposDisponibles(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long miembroId) {
        
        List<EquipmentResponseDTO> equipos = miembroEquipmentService.obtenerEquiposDisponibles(miembroId);
        return ResponseEntity.ok(equipos);
    }

    /**
     * GET /api/miembros/equipos
     * Obtiene todos los equipos del gimnasio.
     */
    @GetMapping("/equipos")
        @Operation(
            summary = "Listar todos los equipos",
            description = "Obtiene todos los equipos (disponibles y no disponibles) desde equipment-service")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de equipos",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = EquipmentResponseDTO.class)))),
        @ApiResponse(responseCode = "503", description = "equipment-service no disponible",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<List<EquipmentResponseDTO>> obtenerTodosLosEquipos() {
        List<EquipmentResponseDTO> equipos = miembroEquipmentService.obtenerTodosLosEquipos();
        return ResponseEntity.ok(equipos);
    }

    /**
     * GET /api/miembros/equipos/status
     * Verifica el estado del servicio de equipos.
     */
    @GetMapping("/equipos/status")
        @Operation(
            summary = "Estado de equipment-service",
            description = "Retorna un resumen simple con disponibilidad del equipment-service")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del servicio",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    public ResponseEntity<Map<String, Object>> verificarServicioEquipos() {
        boolean disponible = miembroEquipmentService.verificarServicioEquipos();
        
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", "equipment-service");
        response.put("disponible", disponible);
        response.put("mensaje", disponible ? "Servicio de equipos operativo" : "Servicio de equipos no disponible");
        
        return ResponseEntity.ok(response);
    }
}
