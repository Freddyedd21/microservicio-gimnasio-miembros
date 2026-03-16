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

import com.analisys.gimnasio.miembros_service.dto.ErrorResponse;
import com.analisys.gimnasio.miembros_service.model.Miembro;
import com.analisys.gimnasio.miembros_service.service.MiembroService;

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

@RestController
@RequestMapping("/api/miembros")
@RequiredArgsConstructor
@Tag(name = "Miembros", description = "API para gestión de miembros del gimnasio")
@SecurityRequirement(name = "bearer-jwt")
public class MiembrosController {
    @Autowired
    private MiembroService miembroService;

    @Operation(summary = "Agregar un nuevo miembro", 
               description = "Crea un nuevo miembro en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro creado exitosamente",
            content = @Content(schema = @Schema(implementation = Miembro.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/agregarMiembro")
        public Miembro agregarMiembro(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos del nuevo miembro",
                required = true,
                content = @Content(schema = @Schema(implementation = Miembro.class)))
            @RequestBody Miembro miembro) {
        return miembroService.agregarMiembro(miembro);
    }

        @Operation(summary = "Obtener todos los miembros",
            description = "Devuelve la lista completa de miembros registrados")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de miembros",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Miembro.class)))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    @GetMapping("/obtenerMiembros")
    public java.util.List<Miembro> obtenerMiembros() {
        return miembroService.obtenerMiembros();
    }

        @Operation(summary = "Obtener miembro por ID",
            description = "Devuelve el miembro asociado al ID provisto")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro devuelto (puede ser null si no existe)",
            content = @Content(schema = @Schema(implementation = Miembro.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    @GetMapping("/obtenerMiembroPorId/{id}")
        public Miembro obtenerMiembroPorId(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long id) {
        return miembroService.obtenerMiembroPorId(id);
    }

        @Operation(summary = "Actualizar miembro",
            description = "Actualiza los datos del miembro asociado al ID provisto")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro actualizado (puede ser null si no existe)",
            content = @Content(schema = @Schema(implementation = Miembro.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    @PutMapping("/actualizarMiembro/{id}")
        public Miembro actualizarMiembro(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Datos a actualizar",
                required = true,
                content = @Content(schema = @Schema(implementation = Miembro.class)))
            @RequestBody Miembro miembro,
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long id) {    
        return miembroService.actualizarMiembro(id, miembro);
    }

        @Operation(summary = "Eliminar miembro",
            description = "Elimina el miembro asociado al ID provisto")
        @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Miembro eliminado"),
        @ApiResponse(responseCode = "500", description = "Error inesperado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "No autenticado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
    @DeleteMapping("/eliminarMiembro/{id}")
        public void eliminarMiembro(
            @Parameter(description = "ID del miembro", required = true, example = "1")
            @PathVariable Long id) {
        miembroService.eliminarMiembro(id);
    }

}
