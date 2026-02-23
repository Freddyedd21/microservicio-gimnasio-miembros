package com.analisys.gimnasio.miembros_service.service;

import com.analisys.gimnasio.miembros_service.client.EquipmentServiceClient;
import com.analisys.gimnasio.miembros_service.dto.EquipmentResponseDTO;
import com.analisys.gimnasio.miembros_service.dto.MiembroEquipmentResponse;
import com.analisys.gimnasio.miembros_service.exception.MiembroNotFoundException;
import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.repository.MiembroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio que orquesta las operaciones entre miembros y equipos.
 * Representa un caso de uso de integración entre microservicios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MiembroEquipmentService {

    private final MiembroRepository miembroRepository;
    private final EquipmentServiceClient equipmentServiceClient;

    // Permite que un miembro use/reserve un equipo del gimnasio.

    public MiembroEquipmentResponse usarEquipo(Long miembroId, Long equipmentId, int cantidad) {
        log.info("Miembro {} solicitando usar equipo {} cantidad {}", miembroId, equipmentId, cantidad);
        
        // Verificar que el miembro existe
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new MiembroNotFoundException(miembroId));
        
        // Llamar al servicio de equipos para usar el equipo
        EquipmentResponseDTO equipmentResponse = equipmentServiceClient.useEquipment(equipmentId, cantidad);
        
        // Construir y retornar la respuesta
        return MiembroEquipmentResponse.builder()
                .mensaje("Equipo reservado exitosamente")
                .miembroNombre(miembro.getNombre())
                .equipoNombre(equipmentResponse.getNombre())
                .cantidadUsada(cantidad)
                .cantidadDisponibleRestante(equipmentResponse.getCantidadDisponible())
                .build();
    }

    // Permite que un miembro libere/devuelva un equipo del gimnasio.

    public MiembroEquipmentResponse liberarEquipo(Long miembroId, Long equipmentId, int cantidad) {
        log.info("Miembro {} liberando equipo {} cantidad {}", miembroId, equipmentId, cantidad);
        
        // Verificar que el miembro existe
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new MiembroNotFoundException(miembroId));
        
        // Llamar al servicio de equipos para liberar el equipo
        EquipmentResponseDTO equipmentResponse = equipmentServiceClient.releaseEquipment(equipmentId, cantidad);
        
        // Construir y retornar la respuesta
        return MiembroEquipmentResponse.builder()
                .mensaje("Equipo devuelto exitosamente")
                .miembroNombre(miembro.getNombre())
                .equipoNombre(equipmentResponse.getNombre())
                .cantidadUsada(cantidad)
                .cantidadDisponibleRestante(equipmentResponse.getCantidadDisponible())
                .build();
    }

    // obtener todos los equipos disponibles para que un miembro pueda usar.

    public List<EquipmentResponseDTO> obtenerEquiposDisponibles(Long miembroId) {
        // Verificar que el miembro existe
        miembroRepository.findById(miembroId)
                .orElseThrow(() -> new MiembroNotFoundException(miembroId));
        
        return equipmentServiceClient.getAvailableEquipment();
    }

    // obtiener todos los equipos (disponibles y no disponibles).

    public List<EquipmentResponseDTO> obtenerTodosLosEquipos() {
        return equipmentServiceClient.getAllEquipment();
    }

    // verifica si el servicio de equipos está disponible.

    public boolean verificarServicioEquipos() {
        return equipmentServiceClient.isServiceAvailable();
    }
}
