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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // --- Métodos de Mapeo Auxiliares ---

    private CalificacionSalidaDto mapToDto(CalificacionEquipo calificacion) {
        return modelMapper.map(calificacion, CalificacionSalidaDto.class);
    }

    private CalificacionEquipo mapToEntity(Object dto) {
        return modelMapper.map(dto, CalificacionEquipo.class);
    }

    /**
     * Recalcula el promedio de calificaciones de un equipo y actualiza la entidad Equipo.
     * NOTA: En un entorno de producción con grandes volúmenes, este método se optimizaría
     * usando una consulta SQL de agregación (SUM/COUNT) en el repositorio para evitar
     * cargar todas las entidades. Aquí se hace de forma ineficiente para el ejemplo.
     * @param equipoId El ID del equipo a recalcular.
     */
    private void actualizarPromedioEquipo(Integer equipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado para actualizar promedio: " + equipoId));

        // 1. Obtener todas las calificaciones (simulando la consulta de suma)
        List<CalificacionEquipo> calificaciones = calificacionEquipoRepository.findByEquipoEvaluadoId(equipoId, Pageable.unpaged())
                .getContent();

        BigDecimal sumaPuntuaciones = BigDecimal.ZERO;
        int totalCalificaciones = calificaciones.size();

        for (CalificacionEquipo calificacion : calificaciones) {
            sumaPuntuaciones = sumaPuntuaciones.add(calificacion.getPuntuacion());
        }

        BigDecimal nuevoPromedio = BigDecimal.ZERO;
        if (totalCalificaciones > 0) {
            // Dividir y redondear a dos decimales
            nuevoPromedio = sumaPuntuaciones.divide(BigDecimal.valueOf(totalCalificaciones), 2, RoundingMode.HALF_UP);
        }

        // 2. Actualizar campos del equipo
        equipo.setCalificacionPromedio(nuevoPromedio);
        equipo.setTotalCalificaciones(totalCalificaciones);

        // 3. Guardar el equipo con el nuevo promedio
        equipoRepository.save(equipo);
    }

    // --- Métodos CRUD y de Consulta ---

    @Override
    public Optional<CalificacionSalidaDto> obtenerPorId(Integer id) {
        return calificacionEquipoRepository.findById(id)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public CalificacionSalidaDto crear(CalificacionCrearDto calificacionCrearDto) {
        // Validar que el equipo exista
        Equipo equipoEvaluado = equipoRepository.findById(calificacionCrearDto.getEquipoEvaluadoId())
                .orElseThrow(() -> new NoSuchElementException("El equipo evaluado con ID " + calificacionCrearDto.getEquipoEvaluadoId() + " no existe."));

        // Validar unicidad (si está ligada a un partido, el evaluador solo califica una vez)
        if (calificacionCrearDto.getPartidoId() != null && calificacionEquipoRepository.existsByPartidoIdAndEvaluadorIdAndEquipoEvaluadoId(
                calificacionCrearDto.getPartidoId(),
                calificacionCrearDto.getEvaluadorId(),
                calificacionCrearDto.getEquipoEvaluadoId())) {
            throw new IllegalStateException("Ya existe una calificación de este evaluador para este equipo en el partido " + calificacionCrearDto.getPartidoId());
        }

        // Mapear y guardar
        CalificacionEquipo calificacion = mapToEntity(calificacionCrearDto);
        calificacion.setEquipoEvaluado(equipoEvaluado); // Establecer la relación ManyToOne

        CalificacionEquipo nuevaCalificacion = calificacionEquipoRepository.save(calificacion);

        // Actualizar el promedio del equipo
        actualizarPromedioEquipo(nuevaCalificacion.getEquipoEvaluado().getId());

        return mapToDto(nuevaCalificacion);
    }

    @Override
    @Transactional
    public CalificacionSalidaDto editar(CalificacionActualizarDto calificacionActualizarDto) {
        CalificacionEquipo calificacionExistente = calificacionEquipoRepository.findById(calificacionActualizarDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Calificación no encontrada con ID: " + calificacionActualizarDto.getId()));

        // Actualizar campos usando ModelMapper
        modelMapper.map(calificacionActualizarDto, calificacionExistente);

        CalificacionEquipo calificacionActualizada = calificacionEquipoRepository.save(calificacionExistente);

        // Actualizar el promedio del equipo
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

        // Actualizar el promedio del equipo después de la eliminación
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