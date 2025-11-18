package org.esfe.servicios.implementaciones;

import org.esfe.dtos.calificacion.CalificacionActualizarDto;
import org.esfe.dtos.calificacion.CalificacionCrearDto;
import org.esfe.dtos.calificacion.CalificacionSalidaDto;
import org.esfe.dtos.equipo.EquipoReferenciaDto;
import org.esfe.modelos.CalificacionEquipo;
import org.esfe.modelos.Equipo;
import org.esfe.repositorios.ICalificacionEquipoRepository;
import org.esfe.repositorios.IEquipoRepository;
import org.esfe.servicios.interfaces.ICalificacionEquipoService;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CalificacionEquipoService implements ICalificacionEquipoService {

    private final ICalificacionEquipoRepository calificacionEquipoRepository;
    private final IEquipoRepository equipoRepository;
    private final ModelMapper modelMapper;

    public CalificacionEquipoService(ICalificacionEquipoRepository calificacionEquipoRepository, IEquipoRepository equipoRepository, ModelMapper modelMapper) {
        this.calificacionEquipoRepository = calificacionEquipoRepository;
        this.equipoRepository = equipoRepository;
        this.modelMapper = modelMapper;
    }

    private CalificacionSalidaDto mapToDto(CalificacionEquipo calificacion) {
        // 1. Mapea la calificación base (propiedades planas)
        CalificacionSalidaDto dto = modelMapper.map(calificacion, CalificacionSalidaDto.class);

        // 2. Mapeo Explícito del ID del Equipo (¡La Corrección!)
        // Se asegura que el ID del equipoEvaluado se asigne al campo equipoEvaluadoId del DTO.
        if (calificacion.getEquipoEvaluado() != null) {
            // Asignamos el ID plano del equipoEvaluado
            dto.setEquipoEvaluadoId(calificacion.getEquipoEvaluado().getId());

            // 3. Mapea y asigna explícitamente el objeto EquipoReferenciaDto
            // El campo 'equipoEvaluado' de la entidad se mapea al campo 'equipo' del DTO de salida
            EquipoReferenciaDto equipoReferenciaDto = modelMapper.map(
                    calificacion.getEquipoEvaluado(), EquipoReferenciaDto.class);
            dto.setEquipo(equipoReferenciaDto);
        } else {
            // Si el equipo es null, ambos campos deberían ser null
            dto.setEquipoEvaluadoId(null);
            dto.setEquipo(null);
        }

        return dto;
    }

    private CalificacionEquipo mapToEntity(Object dto) {
        return modelMapper.map(dto, CalificacionEquipo.class);
    }

    /**
     * Recalcula el promedio y el total de calificaciones de un equipo.
     */
    private void actualizarPromedioEquipo(Integer equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado para actualizar promedio: " + equipoId));

        // 1. Obtener el promedio y el conteo de manera eficiente desde la BD
        Optional<BigDecimal> promedioOpt = calificacionEquipoRepository.calcularPromedioPuntuacion(equipoId);
        Long totalCalificaciones = calificacionEquipoRepository.contarTotalCalificaciones(equipoId);

        // 2. Redondear y establecer el promedio
        BigDecimal nuevoPromedio = promedioOpt
                .map(avg -> avg.setScale(2, RoundingMode.HALF_UP)) // Redondear a dos decimales
                .orElse(BigDecimal.ZERO); // Si no hay calificaciones, el promedio es 0

        // 3. Actualizar campos del equipo
        equipo.setCalificacionPromedio(nuevoPromedio);
        equipo.setTotalCalificaciones(totalCalificaciones.intValue());

        // 4. Guardar el equipo con el nuevo promedio
        equipoRepository.save(equipo);
    }

    /**
     * Lógica central de validación de propiedad. Lanza SecurityException si los IDs no coinciden.
     */
    private void validarPropiedad(CalificacionEquipo calificacion, Integer usuarioAutenticadoId) {
        if (!calificacion.getEvaluadorId().equals(usuarioAutenticadoId)) {
            throw new SecurityException("Acceso denegado: Solo el evaluador original (ID " + calificacion.getEvaluadorId() + ") puede editar o eliminar esta calificación.");
        }
    }

    @Override
    public Optional<CalificacionSalidaDto> obtenerPorId(Integer id) {
        return calificacionEquipoRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public CalificacionSalidaDto crear(CalificacionCrearDto calificacionCrearDto) {
        Equipo equipoEvaluado = equipoRepository.findById(calificacionCrearDto.getEquipoEvaluadoId())
                .orElseThrow(() -> new NoSuchElementException("El equipo evaluado con ID " + calificacionCrearDto.getEquipoEvaluadoId() + " no existe."));

        if (calificacionCrearDto.getPartidoId() != null && calificacionEquipoRepository.existsByPartidoIdAndEvaluadorIdAndEquipoEvaluadoId(
                calificacionCrearDto.getPartidoId(),
                calificacionCrearDto.getEvaluadorId(),
                calificacionCrearDto.getEquipoEvaluadoId())) {
            throw new IllegalStateException("Ya existe una calificación de este evaluador para este equipo en el partido " + calificacionCrearDto.getPartidoId());
        }

        CalificacionEquipo calificacion = mapToEntity(calificacionCrearDto);
        calificacion.setEquipoEvaluado(equipoEvaluado);

        CalificacionEquipo nuevaCalificacion = calificacionEquipoRepository.save(calificacion);

        actualizarPromedioEquipo(nuevaCalificacion.getEquipoEvaluado().getId());

        return mapToDto(nuevaCalificacion);
    }

    @Override
    @Transactional
    public CalificacionSalidaDto editar(CalificacionActualizarDto calificacionActualizarDto, Integer usuarioAutenticadoId) {
        CalificacionEquipo calificacionExistente = calificacionEquipoRepository.findById(calificacionActualizarDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Calificación no encontrada con ID: " + calificacionActualizarDto.getId()));

        // 1. Validar que el usuario autenticado sea el evaluador original
        validarPropiedad(calificacionExistente, usuarioAutenticadoId);

        // 2. Actualizar campos
        modelMapper.map(calificacionActualizarDto, calificacionExistente);

        CalificacionEquipo calificacionActualizada = calificacionEquipoRepository.save(calificacionExistente);

        // 3. Recalcular promedio
        actualizarPromedioEquipo(calificacionActualizada.getEquipoEvaluado().getId());

        return mapToDto(calificacionActualizada);
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id, Integer usuarioAutenticadoId) {
        CalificacionEquipo calificacion = calificacionEquipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Calificación no encontrada con ID: " + id));

        // 1. Validar que el usuario autenticado sea el evaluador original
        validarPropiedad(calificacion, usuarioAutenticadoId);

        Integer equipoId = calificacion.getEquipoEvaluado().getId();

        // 2. Eliminar
        calificacionEquipoRepository.delete(calificacion);

        // 3. Recalcular promedio
        actualizarPromedioEquipo(equipoId);
    }

    @Override
    public Page<CalificacionSalidaDto> obtenerCalificacionesPorEquipo(Integer equipoId, Pageable pageable) {
        return calificacionEquipoRepository.findByEquipoEvaluadoId(equipoId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Page<CalificacionSalidaDto> obtenerCalificacionesPorEvaluador(Integer evaluadorId, Pageable pageable) {
        return calificacionEquipoRepository.findByEvaluadorId(evaluadorId, pageable)
                .map(this::mapToDto);
    }

    @Override
    public Optional<CalificacionSalidaDto> obtenerCalificacionPorUnicidad(Integer partidoId, Integer evaluadorId, Integer equipoId) {
        return calificacionEquipoRepository.findByPartidoIdAndEvaluadorIdAndEquipoEvaluadoId(partidoId, evaluadorId, equipoId)
                .map(this::mapToDto);
    }
}