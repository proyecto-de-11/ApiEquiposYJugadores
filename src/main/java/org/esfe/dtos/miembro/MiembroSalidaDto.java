package org.esfe.dtos.miembro;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.esfe.dtos.equipo.EquipoReferenciaDto;

@Getter
@Setter
public class MiembroSalidaDto implements Serializable {

    private Integer id;
    private Integer equipoId; // Para referencia
    private Integer usuarioId; // ID del usuario externo
    private String rol;
    private Integer numeroCamiseta;
    private String posicion;
    private String estado;
    private LocalDateTime fechaUnion;

    private EquipoReferenciaDto equipo;
}