package org.esfe.servicios.interfaces;

import org.esfe.dtos.invitacion.InvitacionCrearDto;
import org.esfe.dtos.invitacion.InvitacionResponderDto;
import org.esfe.dtos.invitacion.InvitacionSalidaDto;
import org.esfe.enums.EstadoInvitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IInvitacionEquipoService {

    /**
     * Obtiene todas las invitaciones (solo para administración).
     */
    List<InvitacionSalidaDto> obtenerTodas();

    /**
     * Obtiene una invitación específica por su ID.
     */
    Optional<InvitacionSalidaDto> obtenerPorId(Integer id);

    /**
     * Crea una nueva invitación. Incluye validación de cupo del equipo y si ya
     * existe una invitación pendiente.
     */
    InvitacionSalidaDto crear(InvitacionCrearDto invitacionCrearDto);

    /**
     * Elimina una invitación por su ID. Usado para cancelaciones por el remitente o
     * limpieza.
     */
    void eliminarPorId(Integer id);

    //  2. Lógica de Negocio Crucial: Responder Invitación 

    /**
     * Procesa la respuesta de un usuario a una invitación.
     * * REALIZA LAS VALIDACIONES CLAVE:
     * 1. Verifica si la invitación existe y está pendiente.
     * 2. Verifica que el usuario que responde sea el usuario invitado.
     * 3. Si el estado es 'ACEPTADA', valida el cupo del equipo antes de crear la
     * membresía.
     */
    InvitacionSalidaDto responderInvitacion(InvitacionResponderDto invitacionResponderDto);

    //  3. Búsquedas Específicas y Paginación 

    /**
     * Obtiene las invitaciones dirigidas a un usuario específico
     */
    Page<InvitacionSalidaDto> obtenerInvitacionesPorUsuarioYEstado(
            Integer usuarioInvitadoId,
            EstadoInvitacion estado,
            Pageable pageable);

    /**
     * Obtiene todas las invitaciones relacionadas con un equipo específico.
     */
    Page<InvitacionSalidaDto> obtenerInvitacionesPorEquipo(
            Integer equipoId,
            Pageable pageable);

    /**
     * Búsqueda general paginada, útil para filtrado por mensaje.
     */
    Page<InvitacionSalidaDto> obtenerPaginadoYFiltrado(Optional<String> busquedaMensaje, Pageable pageable);
}