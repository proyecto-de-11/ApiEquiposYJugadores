package org.esfe.dtos.calificacion;

import lombok.Getter;
import lombok.Setter;
import org.esfe.dtos.equipo.EquipoReferenciaDto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CalificacionSalidaDto implements Serializable {

    private Integer id;
    private Integer equipoEvaluadoId;
    private Integer evaluadorId;
    private Integer partidoId;

    private BigDecimal puntuacion;
    private String aspectosPositivos;
    private String aspectosMejorar;
    private String comentario;
    private Boolean esAnonimo;
    private LocalDateTime fechaCreacion;

    // Relación de referencia con el Equipo evaluado
    private EquipoReferenciaDto equipo;
}