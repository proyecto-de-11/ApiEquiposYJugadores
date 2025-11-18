package org.esfe.servicios.implementaciones;

import org.esfe.dtos.estadisticas.EstadisticasSalidaDto;
import org.esfe.dtos.equipo.EquipoReferenciaDto;
import org.esfe.modelos.Equipo;
import org.esfe.modelos.EstadisticasEquipo;
import org.esfe.repositorios.IEquipoRepository;
import org.esfe.repositorios.IEstadisticasEquipoRepository;
import org.esfe.servicios.interfaces.IEstadisticasEquipoService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class EstadisticasEquipoService implements IEstadisticasEquipoService {

    private final IEstadisticasEquipoRepository estadisticasRepository;
    private final IEquipoRepository equipoRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EstadisticasEquipoService(
            IEstadisticasEquipoRepository estadisticasRepository,
            IEquipoRepository equipoRepository,
            ModelMapper modelMapper) {
        this.estadisticasRepository = estadisticasRepository;
        this.equipoRepository = equipoRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Mapea la entidad EstadisticasEquipo al DTO de salida.
     */
    private EstadisticasSalidaDto mapToDto(EstadisticasEquipo estadisticas) {
        // 1. Mapeo básico de la entidad al DTO
        EstadisticasSalidaDto dto = modelMapper.map(estadisticas, EstadisticasSalidaDto.class);

        // 2. Mapeo explícito del objeto Equipo anidado
        if (estadisticas.getEquipo() != null) {
            EquipoReferenciaDto equipoDto = modelMapper.map(
                    estadisticas.getEquipo(),
                    EquipoReferenciaDto.class
            );
            dto.setEquipo(equipoDto);
        }

        return dto;
    }

    @Override
    public Optional<EstadisticasSalidaDto> obtenerEstadisticasPorEquipoId(Integer equipoId) {
        return estadisticasRepository.findByEquipoId(equipoId)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public EstadisticasEquipo inicializarEstadisticas(Integer equipoId) {
        // 1. Verificar que el equipo existe
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new NoSuchElementException(
                        "No se puede inicializar estadísticas. Equipo no encontrado con ID: " + equipoId));

        // 2. Verificar que no existan estadísticas previas
        Optional<EstadisticasEquipo> existente = estadisticasRepository.findByEquipoId(equipoId);
        if (existente.isPresent()) {
            throw new IllegalStateException(
                    "Ya existen estadísticas para el equipo con ID: " + equipoId);
        }

        // 3. Crear nueva entidad de estadísticas
        // Los valores por defecto ya están en 0 en la entidad, solo asignamos el equipo
        EstadisticasEquipo nuevasEstadisticas = new EstadisticasEquipo();
        nuevasEstadisticas.setEquipo(equipo);

        // 4. Guardar y retornar
        return estadisticasRepository.save(nuevasEstadisticas);
    }

    @Override
    @Transactional
    public EstadisticasSalidaDto actualizarEstadisticasPorPartido(
            Integer equipoId,
            int golesFavor,
            int golesContra,
            boolean esTorneo,
            String resultado) {

        // 1. Obtener las estadísticas existentes
        EstadisticasEquipo estadisticas = estadisticasRepository.findByEquipoId(equipoId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Estadísticas no encontradas para el equipo con ID: " + equipoId));

        // 2. Validar el resultado
        resultado = resultado.toLowerCase().trim();
        if (!resultado.equals("ganado") && !resultado.equals("perdido") && !resultado.equals("empatado")) {
            throw new IllegalArgumentException(
                    "Resultado inválido. Debe ser 'ganado', 'perdido' o 'empatado'.");
        }

        // 3. Actualizar estadísticas TOTALES (siempre)
        estadisticas.setPartidosJugadosTotal(estadisticas.getPartidosJugadosTotal() + 1);
        estadisticas.setGolesFavorTotal(estadisticas.getGolesFavorTotal() + golesFavor);
        estadisticas.setGolesContraTotal(estadisticas.getGolesContraTotal() + golesContra);

        switch (resultado) {
            case "ganado":
                estadisticas.setPartidosGanadosTotal(estadisticas.getPartidosGanadosTotal() + 1);
                break;
            case "perdido":
                estadisticas.setPartidosPerdidosTotal(estadisticas.getPartidosPerdidosTotal() + 1);
                break;
            case "empatado":
                estadisticas.setPartidosEmpatadosTotal(estadisticas.getPartidosEmpatadosTotal() + 1);
                break;
        }

        // 4. Actualizar estadísticas de TORNEOS (si aplica)
        if (esTorneo) {
            estadisticas.setPartidosJugadosTorneos(estadisticas.getPartidosJugadosTorneos() + 1);
            estadisticas.setGolesFavorTorneos(estadisticas.getGolesFavorTorneos() + golesFavor);
            estadisticas.setGolesContraTorneos(estadisticas.getGolesContraTorneos() + golesContra);

            switch (resultado) {
                case "ganado":
                    estadisticas.setPartidosGanadosTorneos(estadisticas.getPartidosGanadosTorneos() + 1);
                    break;
                case "perdido":
                    estadisticas.setPartidosPerdidosTorneos(estadisticas.getPartidosPerdidosTorneos() + 1);
                    break;
                case "empatado":
                    estadisticas.setPartidosEmpatadosTorneos(estadisticas.getPartidosEmpatadosTorneos() + 1);
                    break;
            }
        }

        // 5. Guardar y retornar DTO
        EstadisticasEquipo actualizado = estadisticasRepository.save(estadisticas);
        return mapToDto(actualizado);
    }

    @Override
    @Transactional
    public EstadisticasSalidaDto incrementarTorneosGanados(Integer equipoId) {
        // 1. Obtener las estadísticas existentes
        EstadisticasEquipo estadisticas = estadisticasRepository.findByEquipoId(equipoId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Estadísticas no encontradas para el equipo con ID: " + equipoId));

        // 2. Incrementar el contador
        estadisticas.setTorneosGanados(estadisticas.getTorneosGanados() + 1);

        // 3. Guardar y retornar DTO
        EstadisticasEquipo actualizado = estadisticasRepository.save(estadisticas);
        return mapToDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminarEstadisticas(Integer equipoId) {
        EstadisticasEquipo estadisticas = estadisticasRepository.findByEquipoId(equipoId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Estadísticas no encontradas para el equipo con ID: " + equipoId));

        estadisticasRepository.delete(estadisticas);
    }
}