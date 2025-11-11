package org.esfe.servicios.implementaciones;

import org.esfe.dtos.equipo.ActivarEquipoDto;
import org.esfe.dtos.equipo.EquipoGuardarDto;
import org.esfe.dtos.equipo.EquipoModificarDto;
import org.esfe.dtos.equipo.EquipoSalidaDto;
import org.esfe.modelos.Equipo;
import org.esfe.repositorios.IEquipoRepository;
import org.esfe.servicios.interfaces.IEquipoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EquipoService implements IEquipoService {

    private final IEquipoRepository equipoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EquipoService(IEquipoRepository equipoRepository, ModelMapper modelMapper) {
        this.equipoRepository = equipoRepository;
        this.modelMapper = modelMapper;
    }

    // --- Métodos de Mapeo Auxiliares ---
    private EquipoSalidaDto mapToDto(Equipo equipo) {
        return modelMapper.map(equipo, EquipoSalidaDto.class);
    }

    private Equipo mapToEntity(Object dto) {
        // Mapea DTOs de entrada (Guardar/Modificar) a la entidad Equipo
        return modelMapper.map(dto, Equipo.class);
    }


    @Override
    public List<EquipoSalidaDto> obtenerTodos() {
        return equipoRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EquipoSalidaDto> obtenerPorId(Integer id) {
        return equipoRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public EquipoSalidaDto crear(EquipoGuardarDto equipoGuardarDto) {
        // 1. Validación de unicidad
        if (equipoRepository.existsByNombreIgnoreCase(equipoGuardarDto.getNombre())) {
            throw new IllegalArgumentException("Ya existe un equipo con el nombre: " + equipoGuardarDto.getNombre());
        }

        // 2. Mapeo y creación
        Equipo equipo = mapToEntity(equipoGuardarDto);
        equipo.setId(null); // Asegurar que es una nueva entidad

        Equipo guardado = equipoRepository.save(equipo);
        return mapToDto(guardado);
    }

    @Override
    public EquipoSalidaDto editar(EquipoModificarDto equipoModificarDto) {
        Equipo existente = equipoRepository.findById(equipoModificarDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + equipoModificarDto.getId()));

        // 1. Validación de unicidad al editar
        Optional<Equipo> equipoMismoNombre = equipoRepository.findByNombreIgnoreCase(equipoModificarDto.getNombre());
        if (equipoMismoNombre.isPresent() && !equipoMismoNombre.get().getId().equals(equipoModificarDto.getId())) {
            throw new IllegalArgumentException("Ya existe otro equipo con el nombre: " + equipoModificarDto.getNombre());
        }

        // 2. Mapear los campos actualizados al existente (ModelMapper se encarga de ignorar nulos y actualizar lo necesario)
        modelMapper.map(equipoModificarDto, existente);

        Equipo actualizado = equipoRepository.save(existente);
        return mapToDto(actualizado);
    }

    @Override
    public void eliminarPorId(Integer id) {
        if (!equipoRepository.existsById(id)) {
            throw new NoSuchElementException("Equipo no encontrado con ID: " + id);
        }
        equipoRepository.deleteById(id);
    }

    @Override
    public EquipoSalidaDto cambiarEstado(ActivarEquipoDto activarEquipoDto) {
        Equipo existente = equipoRepository.findById(activarEquipoDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + activarEquipoDto.getId()));

        existente.setEstaActivo(activarEquipoDto.getEstaActivo());
        Equipo actualizado = equipoRepository.save(existente);
        return mapToDto(actualizado);
    }

    @Override
    public Page<EquipoSalidaDto> obtenerEquiposPaginadosYFiltrados(Optional<String> busqueda, Pageable pageable) {
        // Establecer el orden descendente por ID si no se especifica
        Sort sort = pageable.getSort().isUnsorted() ? Sort.by("id").descending() : pageable.getSort();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Equipo> equipoPage;
        String busquedaTexto = busqueda.orElse("").trim();

        if (busquedaTexto.isEmpty()) {
            // Sin búsqueda, obtener todos paginados
            equipoPage = equipoRepository.findAll(sortedPageable);
        } else {
            // Con búsqueda (por Nombre o Ciudad)
            equipoPage = equipoRepository.findByNombreContainingIgnoreCaseOrCiudadContainingIgnoreCase(
                busquedaTexto, busquedaTexto, sortedPageable
            );
        }

        return equipoPage.map(this::mapToDto);
    }

    @Override
    public Page<EquipoSalidaDto> obtenerEquiposPorTipoDeporte(Integer tipoDeporteId, Pageable pageable) {
        Sort sort = pageable.getSort().isUnsorted() ? Sort.by("id").descending() : pageable.getSort();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Equipo> equipoPage = equipoRepository.findByTipoDeporteId(tipoDeporteId, sortedPageable);

        return equipoPage.map(this::mapToDto);
    }

    @Override
    public Page<EquipoSalidaDto> obtenerEquiposPorCalificacionMinima(Double calificacionMinima, Pageable pageable) {
        Sort sort = pageable.getSort().isUnsorted() ? Sort.by("id").descending() : pageable.getSort();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<Equipo> equipoPage = equipoRepository.findByCalificacionPromedioGreaterThanEqual(calificacionMinima, sortedPageable);

        return equipoPage.map(this::mapToDto);
    }
}