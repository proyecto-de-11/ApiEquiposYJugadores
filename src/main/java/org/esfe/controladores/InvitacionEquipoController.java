package org.esfe.controladores;

import org.esfe.dtos.invitacion.InvitacionCrearDto;
import org.esfe.dtos.invitacion.InvitacionResponderDto;
import org.esfe.dtos.invitacion.InvitacionSalidaDto;
import org.esfe.enums.EstadoInvitacion;
import org.esfe.servicios.interfaces.IInvitacionEquipoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/invitaciones")
public class InvitacionEquipoController {

    private final IInvitacionEquipoService invitacionEquipoService;

    public InvitacionEquipoController(IInvitacionEquipoService invitacionEquipoService) {
        this.invitacionEquipoService = invitacionEquipoService;
    }

    /**
     * Envía una nueva invitación de un equipo a un usuario.
     */
    @PostMapping
    public ResponseEntity<?> crearInvitacion(@Valid @RequestBody InvitacionCrearDto invitacionCrearDto) {
        try {
            InvitacionSalidaDto nuevaInvitacion = invitacionEquipoService.crear(invitacionCrearDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaInvitacion);
        } catch (NoSuchElementException e) {
            // Equipo o usuario remitente/invitado no encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Error de negocio: Equipo lleno, Invitación duplicada, o Usuario ya es miembro
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la invitación: " + e.getMessage());
        }
    }

    /**
     * Permite al usuario invitado aceptar, rechazar o cancelar la invitación.
     */
    @PutMapping("/{id}/respuesta")
    public ResponseEntity<?> responderInvitacion(@PathVariable Integer id, @Valid @RequestBody InvitacionResponderDto respuestaDto) {
        try {
            respuestaDto.setId(id);
            InvitacionSalidaDto actualizada = invitacionEquipoService.responderInvitacion(respuestaDto);
            return ResponseEntity.ok(actualizada);
        } catch (NoSuchElementException e) {
            // Invitación no encontrada
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            // Error de negocio: Invitación ya respondida o Equipo lleno (si intenta ACEPTAR)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (SecurityException e) {
            // Error de seguridad: El usuario que responde no es el invitado
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la respuesta: " + e.getMessage());
        }
    }

    /**
     * Obtiene las invitaciones PENDIENTES dirigidas a un usuario específico (para su dashboard).
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<InvitacionSalidaDto>> obtenerInvitacionesPorUsuario(
            @PathVariable Integer usuarioId,
            @RequestParam(defaultValue = "PENDIENTE") EstadoInvitacion estado,
            Pageable pageable) {

        Page<InvitacionSalidaDto> invitacionesPage =
                invitacionEquipoService.obtenerInvitacionesPorUsuarioYEstado(usuarioId, estado, pageable);

        return ResponseEntity.ok(invitacionesPage);
    }

    /**
     * Obtiene todas las invitaciones enviadas por un equipo.
     */
    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<Page<InvitacionSalidaDto>> obtenerInvitacionesPorEquipo(
            @PathVariable Integer equipoId,
            Pageable pageable) {

        Page<InvitacionSalidaDto> invitacionesPage =
                invitacionEquipoService.obtenerInvitacionesPorEquipo(equipoId, pageable);

        return ResponseEntity.ok(invitacionesPage);
    }

    /**
     * Elimina o Cancela una invitación.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarInvitacion(@PathVariable Integer id) {
        try {
            invitacionEquipoService.eliminarPorId(id);
            return ResponseEntity.ok("Invitación con ID " + id + " eliminada correctamente.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la invitación: " + e.getMessage());
        }
    }

    /**
     * Búsqueda general de invitaciones (ej. por mensaje, para uso administrativo).
     */
    @GetMapping
    public ResponseEntity<Page<InvitacionSalidaDto>> obtenerTodosPaginados(
            @RequestParam Optional<String> busqueda,
            Pageable pageable) {

        Page<InvitacionSalidaDto> invitacionesPage = 
            invitacionEquipoService.obtenerPaginadoYFiltrado(busqueda, pageable);

        return ResponseEntity.ok(invitacionesPage);
    }
}