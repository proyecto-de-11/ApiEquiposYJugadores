package org.esfe.servicios.implementaciones;

import org.esfe.dtos.equipo.EquipoReferenciaDto;
import org.esfe.dtos.miembro.*;
import org.esfe.modelos.Equipo;
import org.esfe.modelos.MiembroEquipo;
import org.esfe.repositorios.IEquipoRepository;
import org.esfe.repositorios.IMiembroEquipoRepository;
import org.esfe.servicios.interfaces.IMiembroEquipoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MiembroEquipoService implements IMiembroEquipoService {

    private final IMiembroEquipoRepository miembroEquipoRepository;
    private final IEquipoRepository equipoRepository; // Necesario para obtener datos del Equipo en el DTO de salida
    private final ModelMapper modelMapper;

    @Autowired
    public MiembroEquipoService(IMiembroEquipoRepository miembroEquipoRepository, IEquipoRepository equipoRepository, ModelMapper modelMapper) {
        this.miembroEquipoRepository = miembroEquipoRepository;
        this.equipoRepository = equipoRepository;
        this.modelMapper = modelMapper;
    }

    private MiembroSalidaDto mapToDto(MiembroEquipo miembro) {
        // Mapeo básico de la entidad al DTO de salida
        MiembroSalidaDto dto = modelMapper.map(miembro, MiembroSalidaDto.class);

        // Mapeo del Equipo anidado (Necesario para MiembroSalidaDto)
        if (miembro.getEquipo() != null) {
            EquipoReferenciaDto equipoDto = modelMapper.map(miembro.getEquipo(), EquipoReferenciaDto.class);
            dto.setEquipo(equipoDto);
        }

        return dto;
    }

    private MiembroEquipo mapToEntity(MiembroCrearDto dto) {
        // Mapeo del DTO de entrada a la entidad
        MiembroEquipo miembro = modelMapper.map(dto, MiembroEquipo.class);

        // Asignar la entidad Equipo al miembro (necesario para guardar la FK)
        Equipo equipo = equipoRepository.findById(dto.getEquipoId())
                .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado con ID: " + dto.getEquipoId()));
        miembro.setEquipo(equipo);

        return miembro;
    }

    @Override
    public MiembroSalidaDto crearMembresia(MiembroCrearDto miembroCrearDto) {
        // 1. Validación de unicidad
        if (esMiembroExistente(miembroCrearDto.getEquipoId(), miembroCrearDto.getUsuarioId())) {
            throw new IllegalArgumentException("El usuario ID " + miembroCrearDto.getUsuarioId() + " ya es miembro del equipo ID " + miembroCrearDto.getEquipoId());
        }

        // 2. Mapeo y creación
        MiembroEquipo nuevoMiembro = mapToEntity(miembroCrearDto);
        nuevoMiembro.setId(null); // Asegurar que es una nueva entidad

        MiembroEquipo guardado = miembroEquipoRepository.save(nuevoMiembro);
        return mapToDto(guardado);
    }

    @Override
    public Optional<MiembroSalidaDto> obtenerPorId(Integer id) {
        return miembroEquipoRepository.findById(id).map(this::mapToDto);
    }

    @Override
    public Page<MiembroSalidaDto> obtenerMiembrosPorEquipo(Integer equipoId, Pageable pageable) {
        Page<MiembroEquipo> miembrosPage = miembroEquipoRepository.findByEquipoId(equipoId, pageable);
        return miembrosPage.map(this::mapToDto);
    }

    @Override
    public Page<MiembroSalidaDto> obtenerMembresiasPorUsuario(Integer usuarioId, Pageable pageable) {
        Page<MiembroEquipo> membresiasPage = miembroEquipoRepository.findByUsuarioId(usuarioId, pageable);
        return membresiasPage.map(this::mapToDto);
    }

    @Override
    public MiembroSalidaDto editarAtributos(MiembroModificarDto miembroModificarDto) {
        MiembroEquipo existente = miembroEquipoRepository.findById(miembroModificarDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Membresía no encontrada con ID: " + miembroModificarDto.getId()));

        // Mapear los campos actualizados al existente (ModelMapper se encarga de ignorar nulos/actualizar lo necesario)
        modelMapper.map(miembroModificarDto, existente);

        MiembroEquipo actualizado = miembroEquipoRepository.save(existente);
        return mapToDto(actualizado);
    }

    @Override
    public MiembroSalidaDto gestionarEstado(MiembroAprobarDto miembroAprobarDto) {
        MiembroEquipo existente = miembroEquipoRepository.findById(miembroAprobarDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Membresía no encontrada con ID: " + miembroAprobarDto.getId()));

        // 1. Actualizar el estado
        existente.setEstado(miembroAprobarDto.getNuevoEstado());

        // 2. Opcional: Actualizar Rol y Camiseta si se proporcionan (típico al aprobar)
        if (miembroAprobarDto.getRolAsignado() != null) {
            existente.setRol(miembroAprobarDto.getRolAsignado());
        }
        if (miembroAprobarDto.getNumeroCamiseta() != null) {
            existente.setNumeroCamiseta(miembroAprobarDto.getNumeroCamiseta());
        }

        MiembroEquipo actualizado = miembroEquipoRepository.save(existente);
        return mapToDto(actualizado);
    }
    @Override
    public void eliminarMembresia(Integer id) {
        if (!miembroEquipoRepository.existsById(id)) {
            throw new NoSuchElementException("Membresía no encontrada con ID: " + id);
        }
        miembroEquipoRepository.deleteById(id);
    }

    @Override
    public boolean esMiembroExistente(Integer equipoId, Integer usuarioId) {
        return miembroEquipoRepository.existsByEquipoIdAndUsuarioId(equipoId, usuarioId);
    }
}