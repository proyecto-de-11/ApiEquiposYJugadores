package org.esfe.repositorios;

import org.esfe.modelos.MiembroEquipo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMiembroEquipoRepository extends JpaRepository<MiembroEquipo, Integer> {

    // 1. Unicidad: Verificar si un usuario ya pertenece a un equipo específico
    boolean existsByEquipoIdAndUsuarioId(Integer equipoId, Integer usuarioId);

    // 2. Obtener un miembro específico
    Optional<MiembroEquipo> findByEquipoIdAndUsuarioId(Integer equipoId, Integer usuarioId);

    // 3. Obtener todos los miembros de un equipo (Paginado)
    Page<MiembroEquipo> findByEquipoId(Integer equipoId, Pageable pageable);

    Page<MiembroEquipo> findByUsuarioId(Integer usuarioId, Pageable pageable);

    // 5. Filtrar por estado y rol dentro de un equipo
    List<MiembroEquipo> findByEquipoIdAndEstadoIgnoreCase(Integer equipoId, String estado);

    List<MiembroEquipo> findByEquipoIdAndRolIgnoreCase(Integer equipoId, String rol);

    long countByEquipoIdAndEstado(Integer equipoId, String estado);

    @Query("SELECT m.equipo.id FROM MiembroEquipo m WHERE m.usuarioId = :usuarioId AND m.estado = 'activo'")
    Page<Integer> findEquipoIdsByUsuarioIdAndEstadoActivo(@Param("usuarioId") Integer usuarioId, Pageable pageable);
}