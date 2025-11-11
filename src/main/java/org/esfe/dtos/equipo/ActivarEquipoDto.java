package org.esfe.dtos.equipo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ActivarEquipoDto implements Serializable {

    @NotNull(message = "El ID es obligatorio.")
    @Min(value = 1, message = "El ID debe ser un valor positivo.")
    private Integer id;
    
    @NotNull(message = "El estado activo es obligatorio.")
    private Boolean estaActivo;
}