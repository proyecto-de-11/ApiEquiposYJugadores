package org.esfe.servicios.interfaces;

import org.esfe.dtos.estadisticas.EstadisticasSalidaDto;
import org.esfe.modelos.EstadisticasEquipo;

import java.util.Optional;

public interface IEstadisticasEquipoService {

    /**
     * Obtiene las estadísticas de un equipo específico por su ID de equipo.
     */
    Optional<EstadisticasSalidaDto> obtenerEstadisticasPorEquipoId(Integer equipoId);

    /**
     * Crea un registro inicial de estadísticas (todos los contadores en cero) para un nuevo equipo.
     * Se llama típicamente después de crear una nueva entidad Equipo.
     */
    EstadisticasEquipo inicializarEstadisticas(Integer equipoId);


    /**
     * Actualiza las estadísticas después de que un partido es jugado por el equipo.
     */
    EstadisticasSalidaDto actualizarEstadisticasPorPartido(
            Integer equipoId,
            int golesFavor,
            int golesContra,
            boolean esTorneo,
            String resultado
    );

    /**
     * Incrementa el contador de torneos ganados por un equipo.
     */
    EstadisticasSalidaDto incrementarTorneosGanados(Integer equipoId);

    /**
     * Elimina un registro de estadísticas (típicamente al eliminar un equipo).
     */
    void eliminarEstadisticas(Integer equipoId);
}