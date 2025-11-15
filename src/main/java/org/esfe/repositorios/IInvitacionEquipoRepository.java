package org.esfe.repositorios;

import org.esfe.modelos.InvitacionEquipo;
import org.esfe.enums.EstadoInvitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IInvitacionEquipoRepository extends JpaRepository<InvitacionEquipo, Integer> {

    /**
     * Buscar todas las invitaciones PENDIENTES dirigidas a un usuario específico.
     * Este es el método clave para el dashboard del usuario.
     */
    Page<InvitacionEquipo> findByUsuarioInvitadoIdAndEstadoOrderByFechaCreacionDesc(
            Integer usuarioInvitadoId,
            EstadoInvitacion estado,
            Pageable pageable);

    Page<InvitacionEquipo> findByEquipoId(Integer equipoId, Pageable pageable);

    Page<InvitacionEquipo> findByEstado(EstadoInvitacion estado, Pageable pageable);

    /**
     * Verificar unicidad: ¿Existe ya una invitación PENDIENTE para este usuario y equipo?
     */
    boolean existsByEquipoIdAndUsuarioInvitadoIdAndEstado(
            Integer equipoId,
            Integer usuarioInvitadoId,
            EstadoInvitacion estado);
    
    Page<InvitacionEquipo> findByMensajeContainingIgnoreCase(
            String mensaje,
            Pageable pageable);
}