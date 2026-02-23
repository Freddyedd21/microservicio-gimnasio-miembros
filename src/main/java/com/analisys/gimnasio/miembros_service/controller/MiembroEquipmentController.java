package com.analisys.gimnasio.miembros_service.controller;

import com.analisys.gimnasio.miembros_service.dto.EquipmentResponseDTO;
import com.analisys.gimnasio.miembros_service.dto.MiembroEquipmentResponse;
import com.analisys.gimnasio.miembros_service.dto.MiembroUseEquipmentRequest;
import com.analisys.gimnasio.miembros_service.service.MiembroEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para las operaciones de miembros con equipos.
 * endpoints para que los miembros puedan usar y liberar equipos.
 */
@RestController
@RequestMapping("/api/miembros")
@RequiredArgsConstructor
public class MiembroEquipmentController {

    private final MiembroEquipmentService miembroEquipmentService;

    /**
     * POST /api/miembros/{miembroId}/equipos/{equipoId}/usar
     * Permite a un miembro usar/reservar un equipo.
     */
    @PostMapping("/{miembroId}/equipos/{equipoId}/usar")
    public ResponseEntity<MiembroEquipmentResponse> usarEquipo(
            @PathVariable Long miembroId,
            @PathVariable Long equipoId,
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
    public ResponseEntity<MiembroEquipmentResponse> liberarEquipo(
            @PathVariable Long miembroId,
            @PathVariable Long equipoId,
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
    public ResponseEntity<List<EquipmentResponseDTO>> obtenerEquiposDisponibles(
            @PathVariable Long miembroId) {
        
        List<EquipmentResponseDTO> equipos = miembroEquipmentService.obtenerEquiposDisponibles(miembroId);
        return ResponseEntity.ok(equipos);
    }

    /**
     * GET /api/miembros/equipos
     * Obtiene todos los equipos del gimnasio.
     */
    @GetMapping("/equipos")
    public ResponseEntity<List<EquipmentResponseDTO>> obtenerTodosLosEquipos() {
        List<EquipmentResponseDTO> equipos = miembroEquipmentService.obtenerTodosLosEquipos();
        return ResponseEntity.ok(equipos);
    }

    /**
     * GET /api/miembros/equipos/status
     * Verifica el estado del servicio de equipos.
     */
    @GetMapping("/equipos/status")
    public ResponseEntity<Map<String, Object>> verificarServicioEquipos() {
        boolean disponible = miembroEquipmentService.verificarServicioEquipos();
        
        Map<String, Object> response = new HashMap<>();
        response.put("servicio", "equipment-service");
        response.put("disponible", disponible);
        response.put("mensaje", disponible ? "Servicio de equipos operativo" : "Servicio de equipos no disponible");
        
        return ResponseEntity.ok(response);
    }
}
