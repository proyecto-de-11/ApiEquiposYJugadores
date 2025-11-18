package org.esfe.controladores;

import org.esfe.dtos.calificacion.CalificacionActualizarDto;
import org.esfe.dtos.calificacion.CalificacionCrearDto;
import org.esfe.dtos.calificacion.CalificacionSalidaDto;
import org.esfe.servicios.interfaces.ICalificacionEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public CalificacionEquipoController(ICalificacionEquipoService calificacionEquipoService) {
        this.calificacionEquipoService = calificacionEquipoService;
    }

    /* * NOTA: Se elimina el método getAuthenticatedUserId() temporal para
     * forzar que el ID del usuario autenticado venga de una cabecera (Header).
     * Esto simula mejor un entorno de microservicios o autenticación externa.
     */

    // --- Métodos de Creación y Consulta ---

    @PostMapping
    public ResponseEntity<?> crearCalificacion(@Valid @RequestBody CalificacionCrearDto calificacionCrearDto) {
        try {
            CalificacionSalidaDto nuevaCalificacion = calificacionEquipoService.crear(calificacionCrearDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCalificacion);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la calificación: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<CalificacionSalidaDto> buscarPorId(@PathVariable Integer id) {
        return calificacionEquipoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<Page<CalificacionSalidaDto>> obtenerCalificacionesPorEquipo(
            @PathVariable Integer equipoId,
            Pageable pageable) {

        Page<CalificacionSalidaDto> calificacionesPage =
                calificacionEquipoService.obtenerCalificacionesPorEquipo(equipoId, pageable);

        return ResponseEntity.ok(calificacionesPage);
    }

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
     * El ID del usuario autenticado se obtiene de la cabecera 'X-User-ID'.
     *
     * @param usuarioAutenticadoId El ID del usuario autenticado, inyectado desde la cabecera X-User-ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editarCalificacion(@PathVariable Integer id,
                                                @Valid @RequestBody CalificacionActualizarDto calificacionActualizarDto,
                                                @RequestHeader(name = "X-User-ID", required = true) Integer usuarioAutenticadoId) {

        try {
            calificacionActualizarDto.setId(id);
            CalificacionSalidaDto actualizado = calificacionEquipoService.editar(calificacionActualizarDto, usuarioAutenticadoId);
            return ResponseEntity.ok(actualizado);
        } catch (SecurityException e) {
            // Maneja el error de acceso denegado (403 Forbidden)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la calificación: " + e.getMessage());
        }
    }

    /**
     * Elimina una calificación existente.
     * El ID del usuario autenticado se obtiene de la cabecera 'X-User-ID'.
     *
     * @param usuarioAutenticadoId El ID del usuario autenticado, inyectado desde la cabecera X-User-ID.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCalificacion(@PathVariable Integer id,
                                                       @RequestHeader(name = "X-User-ID", required = true) Integer usuarioAutenticadoId) {

        try {
            calificacionEquipoService.eliminarPorId(id, usuarioAutenticadoId);
            return ResponseEntity.ok("Calificación eliminada correctamente. El promedio del equipo ha sido recalculado.");
        } catch (SecurityException e) {
            // Maneja el error de acceso denegado (403 Forbidden)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la calificación: " + e.getMessage());
        }
    }
}