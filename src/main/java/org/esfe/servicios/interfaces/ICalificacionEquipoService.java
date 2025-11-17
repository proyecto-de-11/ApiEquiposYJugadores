package org.esfe.servicios.interfaces;

import org.esfe.dtos.calificacion.CalificacionActualizarDto;
import org.esfe.dtos.calificacion.CalificacionCrearDto;
import org.esfe.dtos.calificacion.CalificacionSalidaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ICalificacionEquipoService {

    /**
     * Obtiene una calificación por su ID.
     */
    Optional<CalificacionSalidaDto> obtenerPorId(Integer id);

    /**
     * Crea una nueva calificación y actualiza el promedio del equipo.
     */
    CalificacionSalidaDto crear(CalificacionCrearDto calificacionCrearDto);

    /**
     * Edita una calificación existente y recalcula el promedio del equipo.
     */
    CalificacionSalidaDto editar(CalificacionActualizarDto calificacionActualizarDto);

    /**
     * Elimina una calificación por su ID y recalcula el promedio del equipo.
     * @param id El ID de la calificación a eliminar.
     */
    void eliminarPorId(Integer id);

    /**
     * Obtiene todas las calificaciones de un equipo específico, con paginación.
     */
    Page<CalificacionSalidaDto> obtenerCalificacionesPorEquipo(Integer equipoId, Pageable pageable);

    /**
     * Obtiene todas las calificaciones hechas por un usuario específico, con paginación.
     */
    Page<CalificacionSalidaDto> obtenerCalificacionesPorEvaluador(Integer evaluadorId, Pageable pageable);

    /**
     * Obtiene la calificación que un evaluador dio a un equipo en un partido específico.
     */
    Optional<CalificacionSalidaDto> obtenerCalificacionPorUnicidad(Integer partidoId, Integer evaluadorId, Integer equipoId);
}