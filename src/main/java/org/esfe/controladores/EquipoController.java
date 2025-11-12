package org.esfe.controladores;

import org.esfe.dtos.equipo.ActivarEquipoDto;
import org.esfe.dtos.equipo.EquipoGuardarDto;
import org.esfe.dtos.equipo.EquipoModificarDto;
import org.esfe.dtos.equipo.EquipoSalidaDto;
import org.esfe.servicios.interfaces.IEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {

    private final IEquipoService equipoService;

    @Autowired
    public EquipoController(IEquipoService equipoService) {
        this.equipoService = equipoService;
    }

    @GetMapping
    public ResponseEntity<Page<EquipoSalidaDto>> mostrarTodosPaginadosYFiltrados(
            @RequestParam(required = false) Optional<String> busqueda,
            Pageable pageable) {

        Page<EquipoSalidaDto> equiposPage =
                equipoService.obtenerEquiposPaginadosYFiltrados(busqueda, pageable);

        return ResponseEntity.ok(equiposPage);
    }

    @GetMapping("/filtro/deporte")
    public ResponseEntity<Page<EquipoSalidaDto>> mostrarPorTipoDeporte(
            @RequestParam Integer tipoDeporteId,
            Pageable pageable) {

        Page<EquipoSalidaDto> equiposPage =
                equipoService.obtenerEquiposPorTipoDeporte(tipoDeporteId, pageable);

        return ResponseEntity.ok(equiposPage);
    }
    
    @GetMapping("/filtro/calificacion")
    public ResponseEntity<Page<EquipoSalidaDto>> mostrarPorCalificacionMinima(
            @RequestParam Double minima,
            Pageable pageable) {

        Page<EquipoSalidaDto> equiposPage =
                equipoService.obtenerEquiposPorCalificacionMinima(minima, pageable);

        return ResponseEntity.ok(equiposPage);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EquipoSalidaDto> buscarPorId(@PathVariable Integer id) {
        return equipoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody EquipoGuardarDto equipoGuardarDto) {
        try {
            EquipoSalidaDto nuevoEquipo = equipoService.crear(equipoGuardarDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEquipo);
        } catch (IllegalArgumentException e) {
            // Captura error de negocio (ej. nombre duplicado)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el equipo: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @Valid @RequestBody EquipoModificarDto equipoModificarDto) {
        try {
            // Asegurar que el ID del path coincida con el ID del body
            equipoModificarDto.setId(id);
            EquipoSalidaDto actualizado = equipoService.editar(equipoModificarDto);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar: " + e.getMessage());
        }
    }
    
    /**
     * Cambia el estado activo/inactivo del equipo. Usa un PUT en un subrecurso.
     * Ejemplo: PUT /api/equipos/estado/5 (Body: { "estaActivo": false })
     */
    @PutMapping("/estado/{id}")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id, @Valid @RequestBody ActivarEquipoDto activarEquipoDto) {
        try {
            activarEquipoDto.setId(id);
            EquipoSalidaDto actualizado = equipoService.cambiarEstado(activarEquipoDto);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al cambiar estado: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Integer id) {
        try {
            equipoService.eliminarPorId(id);
            return ResponseEntity.ok("Equipo eliminado correctamente.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar el equipo: " + e.getMessage());
        }
    }
}