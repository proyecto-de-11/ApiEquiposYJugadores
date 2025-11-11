package org.esfe.repositorios;

import org.esfe.modelos.Equipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEquipoRepository extends JpaRepository<Equipo, Integer> {

    // 1. Buscar por nombre exacto (para validaciones de unicidad)
    Optional<Equipo> findByNombreIgnoreCase(String nombre);

    // 2. Búsqueda paginada y filtrada principal (por Nombre O Ciudad)
    Page<Equipo> findByNombreContainingIgnoreCaseOrCiudadContainingIgnoreCase(String nombre, String ciudad, Pageable pageable);

    // 3. Buscar por estado activo/inactivo
    List<Equipo> findByEstaActivo(Boolean estaActivo);

    // 4. Buscar equipos por el ID del usuario que lo creó
    List<Equipo> findByCreadoPor(Integer creadoPor);

    // 5. Verificar si existe un equipo con ese nombre
    boolean existsByNombreIgnoreCase(String nombre);
    
    /**
     * Busca equipos por el ID del tipo de deporte (FK a la otra API).
     */
    Page<Equipo> findByTipoDeporteId(Integer tipoDeporteId, Pageable pageable);

    /**
     * Busca equipos con una calificación promedio mayor o igual al valor dado.
     */
    Page<Equipo> findByCalificacionPromedioGreaterThanEqual(Double calificacionPromedio, Pageable pageable);

    /**
     * Combina la búsqueda general con el filtro por tipo de deporte.
     */
    Page<Equipo> findByTipoDeporteIdAndNombreContainingIgnoreCaseOrTipoDeporteIdAndCiudadContainingIgnoreCase(
        Integer tipoDeporteId1, String nombre,
        Integer tipoDeporteId2, String ciudad,
        Pageable pageable
    );
}