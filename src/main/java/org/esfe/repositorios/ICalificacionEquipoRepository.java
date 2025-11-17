package org.esfe.repositorios;

import org.esfe.modelos.CalificacionEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICalificacionEquipoRepository extends JpaRepository<CalificacionEquipo, Integer> {

    /**
     * Busca todas las calificaciones dadas a un equipo específico, con paginación.
     */
    Page<CalificacionEquipo> findByEquipoEvaluadoId(Integer equipoId, Pageable pageable);

    /**
     * Busca todas las calificaciones realizadas por un evaluador específico, con paginación.
     */
    Page<CalificacionEquipo> findByEvaluadorId(Integer evaluadorId, Pageable pageable);

    /**
     * Busca una calificación específica hecha por un evaluador a un equipo en un partido dado.
     * Esto es crucial para la validación de unicidad por partido.
     */
    Optional<CalificacionEquipo> findByPartidoIdAndEvaluadorIdAndEquipoEvaluadoId(
            Integer partidoId, Integer evaluadorId, Integer equipoEvaluadoId);

    /**
     * Verifica rápidamente si un evaluador ya ha calificado a un equipo en un partido específico.
     */
    boolean existsByPartidoIdAndEvaluadorIdAndEquipoEvaluadoId(
            Integer partidoId, Integer evaluadorId, Integer equipoEvaluadoId);
}