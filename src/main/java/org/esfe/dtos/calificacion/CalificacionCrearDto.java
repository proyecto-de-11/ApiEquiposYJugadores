package org.esfe.dtos.calificacion;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class CalificacionCrearDto implements Serializable {

    @NotNull(message = "El ID del equipo evaluado es obligatorio.")
    @Min(value = 1, message = "El ID del equipo debe ser un valor positivo.")
    private Integer equipoEvaluadoId;

    @NotNull(message = "El ID del evaluador es obligatorio.")
    @Min(value = 1, message = "El ID del evaluador debe ser un valor positivo.")
    private Integer evaluadorId;

    // Opcional, solo si la calificación está ligada a un partido
    @Min(value = 1, message = "El ID del partido debe ser un valor positivo.")
    private Integer partidoId;

    @NotNull(message = "La puntuación es obligatoria.")
    @DecimalMin(value = "1.0", message = "La puntuación mínima es 1.0.")
    @DecimalMax(value = "5.0", message = "La puntuación máxima es 5.0.")
    private BigDecimal puntuacion;

    @Size(max = 1000, message = "Los aspectos positivos no pueden exceder los 1000 caracteres.")
    private String aspectosPositivos; // Mapeado desde JSON en el front

    @Size(max = 1000, message = "Los aspectos a mejorar no pueden exceder los 1000 caracteres.")
    private String aspectosMejorar; // Mapeado desde JSON en el front

    @Size(max = 500, message = "El comentario no puede exceder los 500 caracteres.")
    private String comentario;

    @NotNull(message = "El estado de anonimato es obligatorio.")
    private Boolean esAnonimo = false;
}