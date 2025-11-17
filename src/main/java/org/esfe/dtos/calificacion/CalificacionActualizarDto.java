package org.esfe.dtos.calificacion;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CalificacionActualizarDto implements Serializable {

    @NotNull(message = "El ID de la calificación es obligatorio para la actualización.")
    @Min(value = 1, message = "El ID de la calificación debe ser un valor positivo.")
    private Integer id;

    @NotNull(message = "La puntuación es obligatoria para actualizar.")
    @DecimalMin(value = "1.0", message = "La puntuación mínima es 1.0.")
    @DecimalMax(value = "5.0", message = "La puntuación máxima es 5.0.")
    private BigDecimal puntuacion;

    @Size(max = 1000, message = "Los aspectos positivos no pueden exceder los 1000 caracteres.")
    private String aspectosPositivos;

    @Size(max = 1000, message = "Los aspectos a mejorar no pueden exceder los 1000 caracteres.")
    private String aspectosMejorar;

    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres.")
    private String comentario;

    @NotNull(message = "El estado de anonimato es obligatorio para actualizar.")
    private Boolean esAnonimo;
}