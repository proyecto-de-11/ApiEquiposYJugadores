package org.esfe.controladores;

import org.esfe.dtos.estadisticas.EstadisticasActualizarDto;
import org.esfe.dtos.estadisticas.EstadisticasSalidaDto;
import org.esfe.servicios.interfaces.IEstadisticasEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasEquipoController {

    private final IEstadisticasEquipoService estadisticasEquipoService;

    @Autowired
    public EstadisticasEquipoController(IEstadisticasEquipoService estadisticasEquipoService) {
        this.estadisticasEquipoService = estadisticasEquipoService;
    }

    /**
     * Obtiene las estadísticas de un equipo específico.
     * GET /api/estadisticas/equipo/{equipoId}
     */
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<EstadisticasSalidaDto> obtenerEstadisticasPorEquipo(@PathVariable Integer equipoId) {
        return estadisticasEquipoService.obtenerEstadisticasPorEquipoId(equipoId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Inicializa las estadísticas para un equipo nuevo (todos los contadores en 0).
     * POST /api/estadisticas/inicializar/{equipoId}
     *
     * Este endpoint normalmente sería llamado internamente después de crear un equipo,
     * pero se expone para casos de uso administrativos o de recuperación.
     */
    @PostMapping("/inicializar/{equipoId}")
    public ResponseEntity<?> inicializarEstadisticas(@PathVariable Integer equipoId) {
        try {
            estadisticasEquipoService.inicializarEstadisticas(equipoId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Estadísticas inicializadas correctamente para el equipo " + equipoId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al inicializar estadísticas: " + e.getMessage());
        }
    }

    /**
     * Actualiza las estadísticas después de que un partido es jugado.
     * PUT /api/estadisticas/partido/{equipoId}?golesFavor=3&golesContra=1&esTorneo=true&resultado=ganado
     *
     * Parámetros:
     * - golesFavor: Goles anotados por el equipo
     * - golesContra: Goles recibidos por el equipo
     * - esTorneo: true si es partido de torneo, false si es amistoso
     * - resultado: "ganado", "perdido" o "empatado"
     */
    @PutMapping("/partido/{equipoId}")
    public ResponseEntity<?> actualizarPorPartido(
            @PathVariable Integer equipoId,
            @RequestParam int golesFavor,
            @RequestParam int golesContra,
            @RequestParam(defaultValue = "false") boolean esTorneo,
            @RequestParam String resultado) {

        try {
            EstadisticasSalidaDto actualizado = estadisticasEquipoService.actualizarEstadisticasPorPartido(
                    equipoId, golesFavor, golesContra, esTorneo, resultado);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar estadísticas: " + e.getMessage());
        }
    }

    /**
     * Incrementa el contador de torneos ganados por un equipo.
     * PUT /api/estadisticas/torneo-ganado/{equipoId}
     */
    @PutMapping("/torneo-ganado/{equipoId}")
    public ResponseEntity<?> incrementarTorneosGanados(@PathVariable Integer equipoId) {
        try {
            EstadisticasSalidaDto actualizado = estadisticasEquipoService.incrementarTorneosGanados(equipoId);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al incrementar torneos ganados: " + e.getMessage());
        }
    }

    /**
     * Elimina las estadísticas de un equipo.
     * DELETE /api/estadisticas/equipo/{equipoId}
     *
     * Típicamente usado cuando se elimina un equipo completo.
     */
    @DeleteMapping("/equipo/{equipoId}")
    public ResponseEntity<String> eliminarEstadisticas(@PathVariable Integer equipoId) {
        try {
            estadisticasEquipoService.eliminarEstadisticas(equipoId);
            return ResponseEntity.ok("Estadísticas del equipo " + equipoId + " eliminadas correctamente.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar estadísticas: " + e.getMessage());
        }
    }
}