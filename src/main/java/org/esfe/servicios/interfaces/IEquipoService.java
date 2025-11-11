package org.esfe.servicios.interfaces;

import org.esfe.dtos.equipo.ActivarEquipoDto;
import org.esfe.dtos.equipo.EquipoGuardarDto;
import org.esfe.dtos.equipo.EquipoModificarDto;
import org.esfe.dtos.equipo.EquipoSalidaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IEquipoService {

    List<EquipoSalidaDto> obtenerTodos();
    Optional<EquipoSalidaDto> obtenerPorId(Integer id);
    EquipoSalidaDto crear(EquipoGuardarDto equipoGuardarDto);
    EquipoSalidaDto editar(EquipoModificarDto equipoModificarDto);
    void eliminarPorId(Integer id);

    EquipoSalidaDto cambiarEstado(ActivarEquipoDto activarEquipoDto);

    // Paginación y Filtrado Principal
    Page<EquipoSalidaDto> obtenerEquiposPaginadosYFiltrados(Optional<String> busqueda, Pageable pageable);

    Page<EquipoSalidaDto> obtenerEquiposPorTipoDeporte(Integer tipoDeporteId, Pageable pageable);
    Page<EquipoSalidaDto> obtenerEquiposPorCalificacionMinima(Double calificacionMinima, Pageable pageable);
}