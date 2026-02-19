package com.analisys.gimnasio.miembros_service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.repository.MiembroRepository;

@Component
@ConditionalOnProperty(prefix = "app.dataloader", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataLoader implements ApplicationRunner {

	private final MiembroRepository miembroRepository;

	public DataLoader(MiembroRepository miembroRepository) {
		this.miembroRepository = miembroRepository;
	}

	@Override
	public void run(ApplicationArguments args) {
		List<Miembro> miembrosDemo = List.of(
				crearMiembro("Ana Gómez", "ana.gomez@gimnasio.com", LocalDate.now().minusDays(40)),
				crearMiembro("Carlos Pérez", "carlos.perez@gimnasio.com", LocalDate.now().minusDays(35)),
				crearMiembro("Laura Rodríguez", "laura.rodriguez@gimnasio.com", LocalDate.now().minusDays(30)),
				crearMiembro("Juan Martínez", "juan.martinez@gimnasio.com", LocalDate.now().minusDays(25)),
				crearMiembro("Sofía Ramírez", "sofia.ramirez@gimnasio.com", LocalDate.now().minusDays(20)),
				crearMiembro("Diego Torres", "diego.torres@gimnasio.com", LocalDate.now().minusDays(18)),
				crearMiembro("Valentina Castro", "valentina.castro@gimnasio.com", LocalDate.now().minusDays(15)),
				crearMiembro("Andrés Herrera", "andres.herrera@gimnasio.com", LocalDate.now().minusDays(12)),
				crearMiembro("Camila Vargas", "camila.vargas@gimnasio.com", LocalDate.now().minusDays(10)),
				crearMiembro("Mateo López", "mateo.lopez@gimnasio.com", LocalDate.now().minusDays(7))
		);

		for (Miembro miembro : miembrosDemo) {
			if (!miembroRepository.existsByEmail(miembro.getEmail())) {
				miembroRepository.save(miembro);
			}
		}
	}

	private Miembro crearMiembro(String nombre, String email, LocalDate fechaInscripcion) {
		Miembro miembro = new Miembro();
		miembro.setNombre(nombre);
		miembro.setEmail(email);
		miembro.setFechaInscripcion(fechaInscripcion);
		return miembro;
	}
}
