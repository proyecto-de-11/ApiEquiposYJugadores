package org.esfe.controladores;

import org.esfe.dtos.calificacion.CalificacionActualizarDto;
import org.esfe.dtos.calificacion.CalificacionCrearDto;
import org.esfe.dtos.calificacion.CalificacionSalidaDto;
import org.esfe.servicios.interfaces.ICalificacionEquipoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionEquipoController {

    private final ICalificacionEquipoService calificacionEquipoService;

    public CalificacionEquipoController(ICalificacionEquipoService calificacionEquipoService) {
        this.calificacionEquipoService = calificacionEquipoService;
    }

    /**
     * Crea una nueva calificación para un equipo.
     */
    @PostMapping
    public ResponseEntity<?> crearCalificacion(@Valid @RequestBody CalificacionCrearDto calificacionCrearDto) {
        try {
            CalificacionSalidaDto nuevaCalificacion = calificacionEquipoService.crear(calificacionCrearDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCalificacion);
        } catch (NoSuchElementException e) {
            // Error si el Equipo ID, Partido ID, o Evaluador ID no existe (NOT_FOUND)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            // Error de negocio: El evaluador ya calificó a este equipo en este partido (CONFLICT)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la calificación: " + e.getMessage());
        }
    }

    /**
     * Obtiene una calificación específica por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionSalidaDto> buscarPorId(@PathVariable Integer id) {
        return calificacionEquipoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Obtiene todas las calificaciones recibidas por un equipo específico.
     */
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<Page<CalificacionSalidaDto>> obtenerCalificacionesPorEquipo(
            @PathVariable Integer equipoId,
            Pageable pageable) {

        Page<CalificacionSalidaDto> calificacionesPage =
                calificacionEquipoService.obtenerCalificacionesPorEquipo(equipoId, pageable);

        return ResponseEntity.ok(calificacionesPage);
    }

    /**
     * Obtiene todas las calificaciones emitidas por un usuario evaluador.
     */
    @GetMapping("/evaluador/{evaluadorId}")
    public ResponseEntity<Page<CalificacionSalidaDto>> obtenerCalificacionesPorEvaluador(
            @PathVariable Integer evaluadorId,
            Pageable pageable) {

        Page<CalificacionSalidaDto> calificacionesPage =
                calificacionEquipoService.obtenerCalificacionesPorEvaluador(evaluadorId, pageable);

        return ResponseEntity.ok(calificacionesPage);
    }
    
    /**
     * Edita una calificación existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editarCalificacion(@PathVariable Integer id, @Valid @RequestBody CalificacionActualizarDto calificacionActualizarDto) {
        try {
            calificacionActualizarDto.setId(id);
            CalificacionSalidaDto actualizado = calificacionEquipoService.editar(calificacionActualizarDto);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la calificación: " + e.getMessage());
        }
    }

    /**
     * Elimina una calificación existente y recalcula el promedio del equipo.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCalificacion(@PathVariable Integer id) {
        try {
            calificacionEquipoService.eliminarPorId(id);
            return ResponseEntity.ok("Calificación eliminada correctamente. El promedio del equipo ha sido recalculado.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la calificación: " + e.getMessage());
        }
    }
}