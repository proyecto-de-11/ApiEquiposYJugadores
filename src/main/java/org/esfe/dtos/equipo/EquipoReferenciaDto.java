package org.esfe.dtos.equipo;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class EquipoReferenciaDto implements Serializable {

    private Integer id;
    private String nombre;
    private String logo;
    private String ciudad;
}