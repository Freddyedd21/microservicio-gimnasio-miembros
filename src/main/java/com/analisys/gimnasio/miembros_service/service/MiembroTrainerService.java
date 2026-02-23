package com.analisys.gimnasio.miembros_service.service;

import com.analisys.gimnasio.miembros_service.client.TrainerServiceClient;
import com.analisys.gimnasio.miembros_service.dto.MiembroConEntrenadorDTO;
import com.analisys.gimnasio.miembros_service.dto.TrainerResponseDTO;
import com.analisys.gimnasio.miembros_service.exception.MiembroNotFoundException;
import com.analisys.gimnasio.miembros_service.exception.TrainerServiceException;
import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.repository.MiembroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiembroTrainerService {

    private static final Logger log = LoggerFactory.getLogger(MiembroTrainerService.class);

    private final MiembroRepository miembroRepository;
    private final TrainerServiceClient trainerServiceClient;

    public MiembroTrainerService(MiembroRepository miembroRepository, TrainerServiceClient trainerServiceClient) {
        this.miembroRepository = miembroRepository;
        this.trainerServiceClient = trainerServiceClient;
    }

    /**
     * Asigna un entrenador personal a un miembro
     */
    @Transactional
    public MiembroConEntrenadorDTO asignarEntrenadorPersonal(Long miembroId, Long entrenadorId) {
        log.info("Asignando entrenador personal {} a miembro {}", entrenadorId, miembroId);

        // Verificar que el miembro existe
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new MiembroNotFoundException("Miembro no encontrado con ID: " + miembroId));

        // Verificar que el entrenador existe (validación contra servicio externo)
        TrainerResponseDTO entrenador = trainerServiceClient.getTrainerById(entrenadorId);
        
        // Asignar el entrenador personal
        miembro.setEntrenadorPersonalId(entrenadorId);
        Miembro miembroActualizado = miembroRepository.save(miembro);

        log.info("Entrenador personal {} ({}) asignado a miembro {} ({})", 
                entrenador.getNombre(), entrenadorId, miembro.getNombre(), miembroId);

        return buildMiembroConEntrenadorDTO(miembroActualizado, entrenador);
    }

    /**
     * Obtiene el detalle de un miembro con información del entrenador personal
     */
    public MiembroConEntrenadorDTO obtenerMiembroConEntrenador(Long miembroId) {
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new MiembroNotFoundException("Miembro no encontrado con ID: " + miembroId));

        TrainerResponseDTO entrenador = null;
        if (miembro.getEntrenadorPersonalId() != null) {
            try {
                entrenador = trainerServiceClient.getTrainerById(miembro.getEntrenadorPersonalId());
            } catch (TrainerServiceException e) {
                log.warn("No se pudo obtener info del entrenador {}: {}", miembro.getEntrenadorPersonalId(), e.getMessage());
            }
        }

        return buildMiembroConEntrenadorDTO(miembro, entrenador);
    }

    /**
     * Lista todos los miembros con información de entrenadores personales
     */
    public List<MiembroConEntrenadorDTO> listarMiembrosConEntrenadores() {
        return miembroRepository.findAll().stream()
                .map(miembro -> {
                    TrainerResponseDTO entrenador = null;
                    if (miembro.getEntrenadorPersonalId() != null) {
                        try {
                            entrenador = trainerServiceClient.getTrainerById(miembro.getEntrenadorPersonalId());
                        } catch (TrainerServiceException e) {
                            log.warn("No se pudo obtener entrenador {}", miembro.getEntrenadorPersonalId());
                        }
                    }
                    return buildMiembroConEntrenadorDTO(miembro, entrenador);
                })
                .toList();
    }

    /**
     * Lista miembros asignados a un entrenador específico
     */
    public List<MiembroConEntrenadorDTO> obtenerMiembrosPorEntrenador(Long entrenadorId) {
        // Verificar que el entrenador existe
        TrainerResponseDTO entrenador = trainerServiceClient.getTrainerById(entrenadorId);

        return miembroRepository.findAll().stream()
                .filter(m -> entrenadorId.equals(m.getEntrenadorPersonalId()))
                .map(m -> buildMiembroConEntrenadorDTO(m, entrenador))
                .toList();
    }

    /**
     * Busca entrenadores disponibles por especialidad
     */
    public List<TrainerResponseDTO> buscarEntrenadoresPorEspecialidad(String especialidad) {
        return trainerServiceClient.getTrainersBySpecialty(especialidad);
    }

    /**
     * Quita el entrenador personal de un miembro
     */
    @Transactional
    public Miembro removerEntrenadorPersonal(Long miembroId) {
        Miembro miembro = miembroRepository.findById(miembroId)
                .orElseThrow(() -> new MiembroNotFoundException("Miembro no encontrado con ID: " + miembroId));

        log.info("Removiendo entrenador personal {} de miembro {}", miembro.getEntrenadorPersonalId(), miembroId);
        miembro.setEntrenadorPersonalId(null);
        return miembroRepository.save(miembro);
    }

    /**
     * Obtiene todos los entrenadores disponibles
     */
    public List<TrainerResponseDTO> listarEntrenadoresDisponibles() {
        return trainerServiceClient.getAllTrainers();
    }

    private MiembroConEntrenadorDTO buildMiembroConEntrenadorDTO(Miembro miembro, TrainerResponseDTO entrenador) {
        return MiembroConEntrenadorDTO.builder()
                .id(miembro.getId())
                .nombre(miembro.getNombre())
                .email(miembro.getEmail())
                .fechaInscripcion(miembro.getFechaInscripcion())
                .entrenadorPersonalId(miembro.getEntrenadorPersonalId())
                .entrenadorPersonal(entrenador)
                .build();
    }
}
