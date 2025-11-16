package org.esfe.servicios.implementaciones;

import org.esfe.dtos.invitacion.InvitacionCrearDto;
import org.esfe.dtos.invitacion.InvitacionResponderDto;
import org.esfe.dtos.invitacion.InvitacionSalidaDto;
import org.esfe.dtos.equipo.EquipoReferenciaDto;
import org.esfe.enums.EstadoInvitacion;
import org.esfe.modelos.Equipo;
import org.esfe.modelos.InvitacionEquipo;
import org.esfe.repositorios.IEquipoRepository;
import org.esfe.repositorios.IInvitacionEquipoRepository;
import org.esfe.repositorios.IMiembroEquipoRepository;
import org.esfe.servicios.interfaces.IInvitacionEquipoService;
import org.esfe.servicios.interfaces.IMiembroEquipoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InvitacionEquipoService implements IInvitacionEquipoService {

    private final IInvitacionEquipoRepository invitacionRepository;
    private final IEquipoRepository equipoRepository;
    private final IMiembroEquipoService miembroEquipoService;
    private final IMiembroEquipoRepository miembroEquipoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public InvitacionEquipoService(
            IInvitacionEquipoRepository invitacionRepository,
            IEquipoRepository equipoRepository,
            IMiembroEquipoService miembroEquipoService,
            IMiembroEquipoRepository miembroEquipoRepository,
            ModelMapper modelMapper) {
        this.invitacionRepository = invitacionRepository;
        this.equipoRepository = equipoRepository;
        this.miembroEquipoService = miembroEquipoService;
        this.miembroEquipoRepository = miembroEquipoRepository;
        this.modelMapper = modelMapper;
    }

    //  Mapeadores 

    private InvitacionSalidaDto mapToDto(InvitacionEquipo invitacion) {
        InvitacionSalidaDto dto = modelMapper.map(invitacion, InvitacionSalidaDto.class);

        // Mapeo del Equipo anidado
        if (invitacion.getEquipo() != null) {
            EquipoReferenciaDto equipoDto = modelMapper.map(invitacion.getEquipo(), EquipoReferenciaDto.class);
            dto.setEquipo(equipoDto);
        }
        return dto;
    }

    @Override
    public List<InvitacionSalidaDto> obtenerTodas() {
        return invitacionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<InvitacionSalidaDto> obtenerPorId(Integer id) {
        return invitacionRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public InvitacionSalidaDto crear(InvitacionCrearDto dto) {
        // 1. Obtener la entidad Equipo (necesaria para la FK y validaciones de cupo)
        Equipo equipo = equipoRepository.findById(dto.getEquipoId())
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + dto.getEquipoId()));

        // 2. Validación de Unicidad: No permitir más de una invitación PENDIENTE al
        // mismo usuario
        if (invitacionRepository.existsByEquipoIdAndUsuarioInvitadoIdAndEstado(
                dto.getEquipoId(), dto.getUsuarioInvitadoId(), EstadoInvitacion.PENDIENTE)) {
            throw new IllegalArgumentException("Ya existe una invitación pendiente para este usuario en el equipo.");
        }

        // 3. Validación de Membresía Existente: El usuario ya es miembro (activo o
        // inactivo)
        if (miembroEquipoRepository.existsByEquipoIdAndUsuarioId(dto.getEquipoId(), dto.getUsuarioInvitadoId())) {
            throw new IllegalArgumentException("El usuario ya es miembro de este equipo.");
        }

        // 4. Validación de Cupo
        long miembrosActuales = miembroEquipoRepository.countByEquipoIdAndEstado(dto.getEquipoId(), "activo");
        if (miembrosActuales >= equipo.getMaxMiembros()) {
            throw new IllegalStateException(
                    "El equipo ya alcanzó su límite máximo de miembros. No se puede enviar la invitación.");
        }

        // 5. Mapeo y creación
        InvitacionEquipo nuevaInvitacion = modelMapper.map(dto, InvitacionEquipo.class);
        nuevaInvitacion.setEquipo(equipo); // Asignar la entidad Equipo

        InvitacionEquipo guardada = invitacionRepository.save(nuevaInvitacion);
        return mapToDto(guardada);
    }

    @Override
    public void eliminarPorId(Integer id) {
        if (!invitacionRepository.existsById(id)) {
            throw new NoSuchElementException("Invitación no encontrada con ID: " + id);
        }
        // Se puede añadir lógica de negocio aquí (ej. verificar permisos del usuario
        // que elimina)
        invitacionRepository.deleteById(id);
    }

    //  2. Lógica de Negocio Crucial: Responder Invitación 

    @Override
    public InvitacionSalidaDto responderInvitacion(InvitacionResponderDto dto) {
        InvitacionEquipo invitacion = invitacionRepository.findById(dto.getId())
                .orElseThrow(() -> new NoSuchElementException("Invitación no encontrada."));

        // 1. Validar que la invitación esté pendiente
        if (invitacion.getEstado() != EstadoInvitacion.PENDIENTE) {
            throw new IllegalStateException(
                    "La invitación ya ha sido " + invitacion.getEstado().getValor().toLowerCase() + ".");
        }

        // 2. LÓGICA DE PERMISOS

        boolean permisoDenegado = true;
        Integer usuarioRespuestaId = dto.getUsuarioRespondioId();
        Integer equipoId = invitacion.getEquipo().getId();

        // Heurística para determinar el tipo de solicitud:
        // Si Remitente == Invitado: Asumimos que es una Solicitud de Usuario
        // (USUARIO_SOLICITA).
        if (invitacion.getUsuarioRemitenteId().equals(invitacion.getUsuarioInvitadoId())) {

            // Caso A: Solicitud de Usuario (USUARIO_SOLICITA).
            // Permiso: Cualquier miembro del equipo puede aprobar/rechazar.
            if (miembroEquipoService.esMiembroExistente(equipoId, usuarioRespuestaId)) {
                permisoDenegado = false; // ¡Un miembro del equipo está respondiendo!
            }
        }
        // Si Remitente != Invitado: Asumimos que es una Invitación de Equipo
        // (EQUIPO_INVITA).
        else {

            // Caso B: Invitación de Equipo (EQUIPO_INVITA).
            // Permiso: SOLO el usuario invitado puede responder.
            if (invitacion.getUsuarioInvitadoId().equals(usuarioRespuestaId)) {
                permisoDenegado = false; // ¡El invitado está respondiendo!
            }
        }

        if (permisoDenegado) {
            throw new SecurityException("No tiene permiso para responder esta invitación.");
        }

        // 3. Procesar el nuevo estado de la respuesta
        if (dto.getNuevoEstado() == EstadoInvitacion.ACEPTADA) {

            // a. VALIDACIÓN CLAVE: Verificar si el equipo está lleno
            Equipo equipo = invitacion.getEquipo();
            long miembrosActivos = miembroEquipoRepository.countByEquipoIdAndEstado(equipo.getId(), "activo");

            if (miembrosActivos >= equipo.getMaxMiembros()) {
                // Se lanza una excepción para notificar al usuario (más limpio que marcar como
                // rechazada)
                throw new IllegalStateException(
                        "El equipo " + equipo.getNombre() + " ya está lleno. La invitación no puede ser aceptada.");
            }

            // b. Crear la membresía (Llama al servicio de miembros)
            miembroEquipoService.crearMembresiaDesdeInvitacion(
                    invitacion.getEquipo().getId(),
                    invitacion.getUsuarioInvitadoId());

            // c. Si la membresía se crea con éxito, actualizar el estado de la invitación
            invitacion.setEstado(EstadoInvitacion.ACEPTADA);

        } else if (dto.getNuevoEstado() == EstadoInvitacion.RECHAZADA
                || dto.getNuevoEstado() == EstadoInvitacion.CANCELADA) {
            // 4. Procesar Rechazo o Cancelación
            invitacion.setEstado(dto.getNuevoEstado());
        } else {
            throw new IllegalArgumentException("Estado de respuesta no válido: " + dto.getNuevoEstado().getValor());
        }

        // 5. Finalizar
        invitacion.setUsuarioRespondioId(dto.getUsuarioRespondioId());
        invitacion.setFechaRespuesta(LocalDateTime.now());

        InvitacionEquipo actualizada = invitacionRepository.save(invitacion);
        return mapToDto(actualizada);
    }

    @Override
    public Page<InvitacionSalidaDto> obtenerInvitacionesPorUsuarioYEstado(
            Integer usuarioInvitadoId,
            EstadoInvitacion estado,
            Pageable pageable) {
        Page<InvitacionEquipo> invitacionesPage = invitacionRepository
                .findByUsuarioInvitadoIdAndEstadoOrderByFechaCreacionDesc(usuarioInvitadoId, estado, pageable);
        return invitacionesPage.map(this::mapToDto);
    }

    @Override
    public Page<InvitacionSalidaDto> obtenerInvitacionesPorEquipo(Integer equipoId, Pageable pageable) {
        Page<InvitacionEquipo> invitacionesPage = invitacionRepository.findByEquipoId(equipoId, pageable);
        return invitacionesPage.map(this::mapToDto);
    }

    @Override
    public Page<InvitacionSalidaDto> obtenerPaginadoYFiltrado(Optional<String> busquedaMensaje, Pageable pageable) {
        Page<InvitacionEquipo> invitacionesPage;

        if (busquedaMensaje.isPresent() && !busquedaMensaje.get().isEmpty()) {
            // Búsqueda por mensaje
            invitacionesPage = invitacionRepository.findByMensajeContainingIgnoreCase(busquedaMensaje.get(), pageable);
        } else {
            // Obtener todos, ordenados por ID descendente por defecto (más reciente
            // primero)
            invitacionesPage = invitacionRepository.findAll(pageable);
        }

        return invitacionesPage.map(this::mapToDto);
    }
}