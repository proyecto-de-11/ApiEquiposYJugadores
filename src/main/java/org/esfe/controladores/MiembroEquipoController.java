package org.esfe.controladores;

import org.esfe.dtos.miembro.*;
import org.esfe.servicios.interfaces.IMiembroEquipoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/miembros")
public class MiembroEquipoController {

    private final IMiembroEquipoService miembroEquipoService;

    @Autowired
    public MiembroEquipoController(IMiembroEquipoService miembroEquipoService) {
        this.miembroEquipoService = miembroEquipoService;
    }

    @PostMapping
    public ResponseEntity<?> crearMembresia(@Valid @RequestBody MiembroCrearDto miembroCrearDto) {
        try {
            MiembroSalidaDto nuevaMembresia = miembroEquipoService.crearMembresia(miembroCrearDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaMembresia);
        } catch (NoSuchElementException e) {
            // Error si el Equipo ID no existe
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Error de negocio (ej. ya es miembro)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la membresía: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MiembroSalidaDto> buscarPorId(@PathVariable Integer id) {
        return miembroEquipoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/equipo/{equipoId}")
    public ResponseEntity<Page<MiembroSalidaDto>> mostrarMiembrosPorEquipo(
            @PathVariable Integer equipoId,
            Pageable pageable) {

        Page<MiembroSalidaDto> miembrosPage =
                miembroEquipoService.obtenerMiembrosPorEquipo(equipoId, pageable);

        return ResponseEntity.ok(miembrosPage);
    }

    /**
     * Obtiene todos los equipos a los que pertenece un usuario.
     * GET /api/miembros/usuario/20?page=0&size=5
     */
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<MiembroSalidaDto>> mostrarMembresiasPorUsuario(
            @PathVariable Integer usuarioId,
            Pageable pageable) {

        Page<MiembroSalidaDto> membresiasPage =
                miembroEquipoService.obtenerMembresiasPorUsuario(usuarioId, pageable);

        return ResponseEntity.ok(membresiasPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarAtributos(@PathVariable Integer id, @Valid @RequestBody MiembroModificarDto miembroModificarDto) {
        try {
            miembroModificarDto.setId(id);
            MiembroSalidaDto actualizado = miembroEquipoService.editarAtributos(miembroModificarDto);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar atributos de la membresía: " + e.getMessage());
        }
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<?> gestionarEstado(@PathVariable Integer id, @Valid @RequestBody MiembroAprobarDto miembroAprobarDto) {
        try {
            miembroAprobarDto.setId(id);
            MiembroSalidaDto actualizado = miembroEquipoService.gestionarEstado(miembroAprobarDto);
            return ResponseEntity.ok(actualizado);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al gestionar el estado de la membresía: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarMembresia(@PathVariable Integer id) {
        try {
            miembroEquipoService.eliminarMembresia(id);
            return ResponseEntity.ok("Membresía eliminada correctamente (usuario expulsado o abandono).");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la membresía: " + e.getMessage());
        }
    }
}