package org.esfe.repositorios;

import org.esfe.modelos.EstadisticasEquipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEstadisticasEquipoRepository extends JpaRepository<EstadisticasEquipo, Integer> {

    /**
     * Busca las estadísticas de un equipo específico utilizando el ID de la entidad Equipo.
     *
     * Dado que en el modelo EstadisticasEquipo, 'equipo' es la entidad relacionada
     * y la columna 'equipo_id' es única, este método recupera el registro único
     * de estadísticas asociado a ese ID.
     */
    Optional<EstadisticasEquipo> findByEquipoId(Integer equipoId);
}