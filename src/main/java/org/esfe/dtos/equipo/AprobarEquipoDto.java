package org.esfe.dtos.equipo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AprobarEquipoDto implements Serializable {

    @NotNull(message = "El ID del equipo es obligatorio.")
    @Min(value = 1, message = "El ID debe ser un valor positivo.")
    private Integer id;

    // El valor que se desea establecer: true si requiere aprobación, false si ya no.
    @NotNull(message = "El estado de requerimiento de aprobación es obligatorio.")
    private Boolean requiereAprobacion;
}