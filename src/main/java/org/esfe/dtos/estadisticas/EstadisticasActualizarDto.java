package org.esfe.dtos.estadisticas;

import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;

@Getter
@Setter
public class EstadisticasActualizarDto implements Serializable {

    // Se necesita el ID para identificar el registro a actualizar
    @NotNull(message = "El ID de las estadísticas es obligatorio para la actualización.")
    private Integer id;

    @PositiveOrZero
    private Integer partidosJugadosTotal;

    @PositiveOrZero
    private Integer partidosGanadosTotal;

    @PositiveOrZero
    private Integer torneosGanados;
}