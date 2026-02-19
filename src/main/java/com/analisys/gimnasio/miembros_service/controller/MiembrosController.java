package com.analisys.gimnasio.miembros_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.service.MiembroService;

@RestController
@RequestMapping("/api/miembros")
public class MiembrosController {
    @Autowired
    private MiembroService miembroService;

    @PostMapping("/agregarMiembro")
    public Miembro agregarMiembro(@RequestBody Miembro miembro) {
        return miembroService.agregarMiembro(miembro);
    }

    @GetMapping("/obtenerMiembros")
    public java.util.List<Miembro> obtenerMiembros() {
        return miembroService.obtenerMiembros();
    }

    @GetMapping("/obtenerMiembroPorId/{id}")
    public Miembro obtenerMiembroPorId(@PathVariable Long id) {
        return miembroService.obtenerMiembroPorId(id);
    }

    @PutMapping("/actualizarMiembro/{id}")
    public Miembro actualizarMiembro(@RequestBody Miembro miembro, @PathVariable Long id) {    
        return miembroService.actualizarMiembro(id, miembro);
    }

    @DeleteMapping("/eliminarMiembro/{id}")
    public void eliminarMiembro(@PathVariable Long id) {
        miembroService.eliminarMiembro(id);
    }

}
