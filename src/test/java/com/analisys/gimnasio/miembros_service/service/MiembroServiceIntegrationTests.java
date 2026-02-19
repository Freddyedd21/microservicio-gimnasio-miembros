package com.analisys.gimnasio.miembros_service.service;

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
class MiembroServiceIntegrationTests {

    @Autowired
    private MiembroService miembroService;

    @Autowired
    private MiembroRepository miembroRepository;

    @BeforeEach
    void setUp() {
        miembroRepository.deleteAll();
    }

    @Test
    void agregarMiembro_guardaYRetornaConId() {
        Miembro nuevo = new Miembro();
        nuevo.setNombre("Prueba Uno");
        nuevo.setEmail("prueba1@gimnasio.com");
        nuevo.setFechaInscripcion(LocalDate.of(2026, 2, 1));

        Miembro guardado = miembroService.agregarMiembro(nuevo);

        assertNotNull(guardado.getId());
        assertEquals("Prueba Uno", guardado.getNombre());
        assertEquals("prueba1@gimnasio.com", guardado.getEmail());
        assertEquals(LocalDate.of(2026, 2, 1), guardado.getFechaInscripcion());
    }

    @Test
    void obtenerMiembros_retornaListaConElementos() {
        Miembro m1 = new Miembro();
        m1.setNombre("A");
        m1.setEmail("a@gimnasio.com");
        m1.setFechaInscripcion(LocalDate.now());

        Miembro m2 = new Miembro();
        m2.setNombre("B");
        m2.setEmail("b@gimnasio.com");
        m2.setFechaInscripcion(LocalDate.now());

        miembroService.agregarMiembro(m1);
        miembroService.agregarMiembro(m2);

        List<Miembro> miembros = miembroService.obtenerMiembros();

        assertEquals(2, miembros.size());
    }

    @Test
    void obtenerMiembroPorId_retornaMiembroExistente() {
        Miembro nuevo = new Miembro();
        nuevo.setNombre("Carlos");
        nuevo.setEmail("carlos@test.com");
        nuevo.setFechaInscripcion(LocalDate.of(2026, 1, 10));

        Miembro guardado = miembroService.agregarMiembro(nuevo);

        Miembro encontrado = miembroService.obtenerMiembroPorId(guardado.getId());

        assertNotNull(encontrado);
        assertEquals(guardado.getId(), encontrado.getId());
        assertEquals("Carlos", encontrado.getNombre());
    }

    @Test
    void actualizarMiembro_modificaCampos() {
        Miembro nuevo = new Miembro();
        nuevo.setNombre("Nombre Viejo");
        nuevo.setEmail("viejo@test.com");
        nuevo.setFechaInscripcion(LocalDate.of(2026, 1, 1));
        Miembro guardado = miembroService.agregarMiembro(nuevo);

        Miembro actualizado = new Miembro();
        actualizado.setNombre("Nombre Nuevo");
        actualizado.setEmail("nuevo@test.com");
        actualizado.setFechaInscripcion(LocalDate.of(2026, 2, 2));

        Miembro resultado = miembroService.actualizarMiembro(guardado.getId(), actualizado);

        assertNotNull(resultado);
        assertEquals("Nombre Nuevo", resultado.getNombre());
        assertEquals("nuevo@test.com", resultado.getEmail());
        assertEquals(LocalDate.of(2026, 2, 2), resultado.getFechaInscripcion());
    }

    @Test
    void eliminarMiembro_borraRegistro() {
        Miembro nuevo = new Miembro();
        nuevo.setNombre("Eliminar");
        nuevo.setEmail("eliminar@test.com");
        nuevo.setFechaInscripcion(LocalDate.now());
        Miembro guardado = miembroService.agregarMiembro(nuevo);

        miembroService.eliminarMiembro(guardado.getId());

        assertTrue(miembroRepository.findById(guardado.getId()).isEmpty());
    }
}
