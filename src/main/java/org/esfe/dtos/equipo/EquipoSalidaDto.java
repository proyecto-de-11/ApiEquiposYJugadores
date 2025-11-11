package org.esfe.dtos.equipo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class EquipoSalidaDto implements Serializable {
    
    private Integer id;
    private String nombre;
    private Integer creadoPor;
    private Integer tipoDeporteId;
    private String descripcion;
    private String logo;
    private String colorPrincipal;
    private String colorSecundario;
    private String ciudad;
    private String nivel;
    private Integer maxMiembros;
    private Boolean requiereAprobacion;
    private Double calificacionPromedio;
    private Integer totalCalificaciones;
    private Boolean estaActivo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}