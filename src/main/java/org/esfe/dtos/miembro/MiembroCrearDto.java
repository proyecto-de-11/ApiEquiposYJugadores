package org.esfe.dtos.miembro;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class MiembroCrearDto implements Serializable {

    @NotNull(message = "El ID del equipo es obligatorio.")
    @Min(value = 1, message = "El ID del equipo debe ser positivo.")
    private Integer equipoId;

    @NotNull(message = "El ID del usuario es obligatorio.")
    @Min(value = 1, message = "El ID del usuario debe ser positivo.")
    private Integer usuarioId;

    // Opcional al crear, pero si lo incluye, se valida el rol y estado inicial
    @Pattern(regexp = "capitan|vice_capitan|jugador", message = "Rol inválido. Debe ser: capitan, vice_capitan o jugador.")
    private String rol = "jugador";

    @Min(value = 1, message = "El número de camiseta debe ser al menos 1.")
    private Integer numeroCamiseta;

    @Size(max = 100, message = "La posición no puede exceder los 100 caracteres.")
    private String posicion;

    @Pattern(regexp = "activo|inactivo|suspendido", message = "Estado inválido. Debe ser: activo, inactivo o suspendido.")
    private String estado = "activo"; // El estado inicial al crear/solicitar
}