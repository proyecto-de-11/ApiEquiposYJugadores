package org.esfe.dtos.miembro;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MiembroAprobarDto implements Serializable {

    @NotNull(message = "El ID de la membresía (miembro) es obligatorio.")
    @Min(value = 1, message = "El ID de la membresía debe ser positivo.")
    private Integer id;

    @NotNull(message = "El nuevo estado es obligatorio.")
    @Pattern(regexp = "activo|rechazado|suspendido", message = "El estado de aprobación debe ser 'activo', 'rechazado' o 'suspendido'.")
    private String nuevoEstado;

    @Pattern(regexp = "capitan|vice_capitan|jugador", message = "Rol inválido. Debe ser: capitan, vice_capitan o jugador.")
    private String rolAsignado;

}