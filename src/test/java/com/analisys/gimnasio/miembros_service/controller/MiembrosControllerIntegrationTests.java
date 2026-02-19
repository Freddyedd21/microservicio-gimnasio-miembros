package com.analisys.gimnasio.miembros_service.controller;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.repository.MiembroRepository;

@SpringBootTest
@Transactional
class MiembrosControllerIntegrationTests {

    @Autowired
    private MiembrosController miembrosController;

    @Autowired
    private MiembroRepository miembroRepository;

    @BeforeEach
    void setUp() {
        miembroRepository.deleteAll();
    }

    @Test
    void agregarMiembro_yObtenerMiembros_funciona() {
        Miembro miembro = new Miembro();
        miembro.setNombre("Controller Test");
        miembro.setEmail("controller@test.com");
        miembro.setFechaInscripcion(LocalDate.of(2026, 2, 1));

        Miembro guardado = miembrosController.agregarMiembro(miembro);
        assertNotNull(guardado.getId());

        List<Miembro> miembros = miembrosController.obtenerMiembros();
        assertEquals(1, miembros.size());
        assertEquals("controller@test.com", miembros.get(0).getEmail());
    }

    @Test
    void obtenerMiembroPorId_retornaMiembro() {
        Miembro miembro = new Miembro();
        miembro.setNombre("Buscar");
        miembro.setEmail("buscar@test.com");
        miembro.setFechaInscripcion(LocalDate.of(2026, 1, 10));
        Miembro guardado = miembroRepository.save(miembro);

        Miembro encontrado = miembrosController.obtenerMiembroPorId(guardado.getId());
        assertNotNull(encontrado);
        assertEquals("buscar@test.com", encontrado.getEmail());
    }

    @Test
    void actualizarMiembro_modificaCampos() {
        Miembro miembro = new Miembro();
        miembro.setNombre("Viejo");
        miembro.setEmail("viejo@ctrl.com");
        miembro.setFechaInscripcion(LocalDate.of(2026, 1, 1));
        Miembro guardado = miembroRepository.save(miembro);

        Miembro update = new Miembro();
        update.setNombre("Nuevo");
        update.setEmail("nuevo@ctrl.com");
        update.setFechaInscripcion(LocalDate.of(2026, 2, 2));

        Miembro actualizado = miembrosController.actualizarMiembro(update, guardado.getId());
        assertNotNull(actualizado);
        assertEquals("Nuevo", actualizado.getNombre());
        assertEquals("nuevo@ctrl.com", actualizado.getEmail());
        assertEquals(LocalDate.of(2026, 2, 2), actualizado.getFechaInscripcion());
    }

    @Test
    void eliminarMiembro_borraRegistro() {
        Miembro miembro = new Miembro();
        miembro.setNombre("Eliminar");
        miembro.setEmail("delete@ctrl.com");
        miembro.setFechaInscripcion(LocalDate.of(2026, 1, 20));
        Miembro guardado = miembroRepository.save(miembro);

        miembrosController.eliminarMiembro(guardado.getId());

        assertTrue(miembroRepository.findById(guardado.getId()).isEmpty());
    }
}
