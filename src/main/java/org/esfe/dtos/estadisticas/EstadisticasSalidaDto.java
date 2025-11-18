package org.esfe.dtos.estadisticas;

import lombok.Getter;
import lombok.Setter;
import org.esfe.dtos.equipo.EquipoReferenciaDto;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class EstadisticasSalidaDto implements Serializable {

    private Integer id;

    // Relación OneToOne con una referencia del equipo
    private EquipoReferenciaDto equipo;

    // --- Totales Generales ---
    private Integer partidosJugadosTotal;
    private Integer partidosGanadosTotal;
    private Integer partidosPerdidosTotal;
    private Integer partidosEmpatadosTotal;
    private Integer golesFavorTotal;
    private Integer golesContraTotal;

    // --- Totales de Torneos ---
    private Integer partidosJugadosTorneos;
    private Integer partidosGanadosTorneos;
    private Integer partidosPerdidosTorneos;
    private Integer partidosEmpatadosTorneos;
    private Integer golesFavorTorneos;
    private Integer golesContraTorneos;

    // --- Trofeos ---
    private Integer torneosGanados;

    private LocalDateTime fechaActualizacion;
}