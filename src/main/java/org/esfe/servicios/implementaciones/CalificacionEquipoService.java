package org.esfe.servicios.implementaciones;

import org.esfe.dtos.calificacion.CalificacionActualizarDto;
import org.esfe.dtos.calificacion.CalificacionCrearDto;
import org.esfe.dtos.calificacion.CalificacionSalidaDto;
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
        return modelMapper.map(calificacion, CalificacionSalidaDto.class);
    }

    private CalificacionEquipo mapToEntity(Object dto) {
        return modelMapper.map(dto, CalificacionEquipo.class);
    }

    /**
     * Recalcula el promedio y el total de calificaciones de un equipo de manera eficiente
     * usando consultas de agregación JPQL (asumiendo que ICalificacionEquipoRepository
     * tiene los métodos calcularPromedioPuntuacion y contarTotalCalificaciones).
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

    @Override
    public Optional<CalificacionSalidaDto> obtenerPorId(Integer id) {
        return calificacionEquipoRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public CalificacionSalidaDto crear(CalificacionCrearDto calificacionCrearDto) {
        // 1. Validar que el equipo exista
        Equipo equipoEvaluado = equipoRepository.findById(calificacionCrearDto.getEquipoEvaluadoId())
                .orElseThrow(() -> new NoSuchElementException("El equipo evaluado con ID " + calificacionCrearDto.getEquipoEvaluadoId() + " no existe."));

        // 2. Validar unicidad (si está ligada a un partido)
        if (calificacionCrearDto.getPartidoId() != null && calificacionEquipoRepository.existsByPartidoIdAndEvaluadorIdAndEquipoEvaluadoId(
                calificacionCrearDto.getPartidoId(),
                calificacionCrearDto.getEvaluadorId(),
                calificacionCrearDto.getEquipoEvaluadoId())) {
            throw new IllegalStateException("Ya existe una calificación de este evaluador para este equipo en el partido " + calificacionCrearDto.getPartidoId());
        }

        // 3. Mapear y guardar
        CalificacionEquipo calificacion = mapToEntity(calificacionCrearDto);
        calificacion.setEquipoEvaluado(equipoEvaluado); // Establecer la relación ManyToOne
        
        CalificacionEquipo nuevaCalificacion = calificacionEquipoRepository.save(calificacion);
        
        // 4. Actualizar el promedio del equipo de manera eficiente
        actualizarPromedioEquipo(nuevaCalificacion.getEquipoEvaluado().getId());

        return mapToDto(nuevaCalificacion);
    }

    @Override
    @Transactional
    public CalificacionSalidaDto editar(CalificacionActualizarDto calificacionActualizarDto) {
        CalificacionEquipo calificacionExistente = calificacionEquipoRepository.findById(calificacionActualizarDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Calificación no encontrada con ID: " + calificacionActualizarDto.getId()));

        // 1. Actualizar campos usando ModelMapper
        modelMapper.map(calificacionActualizarDto, calificacionExistente);
        
        CalificacionEquipo calificacionActualizada = calificacionEquipoRepository.save(calificacionExistente);
        
        // 2. Actualizar el promedio del equipo de manera eficiente
        actualizarPromedioEquipo(calificacionActualizada.getEquipoEvaluado().getId());

        return mapToDto(calificacionActualizada);
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id) {
        CalificacionEquipo calificacion = calificacionEquipoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Calificación no encontrada con ID: " + id));

        Integer equipoId = calificacion.getEquipoEvaluado().getId();
        
        calificacionEquipoRepository.delete(calificacion);

        // Actualizar el promedio del equipo de manera eficiente
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