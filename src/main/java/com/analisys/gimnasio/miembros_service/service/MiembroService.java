package com.analisys.gimnasio.miembros_service.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 

import com.analisys.gimnasio.miembros_service.messaging.publisher.MiembroEventsPublisher;
import com.analisys.gimnasio.miembros_service.model.Miembro;
import  com.analisys.gimnasio.miembros_service.repository.MiembroRepository;

@Service
public class MiembroService {
    @Autowired
    private MiembroRepository miembroRepository;

    @Autowired
    private MiembroEventsPublisher miembroEventsPublisher;

    public Miembro agregarMiembro(Miembro miembro) {
        for (Miembro m : miembroRepository.findAll()) {
            if (m.getEmail().equalsIgnoreCase(miembro.getEmail())) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
        }
        Miembro guardado = miembroRepository.save(miembro);
        miembroEventsPublisher.publishMiembroCreado(guardado);
        return guardado;
    }

    public List<Miembro> obtenerMiembros() {
        return miembroRepository.findAll();
    }

    public Miembro obtenerMiembroPorId(Long id) {
        return miembroRepository.findById(id).orElse(null);
    }

    public Miembro actualizarMiembro(Long id, Miembro miembroActualizado) {
        Miembro miembroExistente = miembroRepository.findById(id).orElse(null);
        if (miembroExistente != null) {
            miembroExistente.setNombre(miembroActualizado.getNombre());
            miembroExistente.setEmail(miembroActualizado.getEmail());
            miembroExistente.setFechaInscripcion(miembroActualizado.getFechaInscripcion());
            return miembroRepository.save(miembroExistente);
        }
        return null;
    }


    public void eliminarMiembro(Long id) {
        miembroRepository.deleteById(id);
    }

    


}
