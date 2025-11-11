package org.esfe.dtos.equipo;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class EquipoGuardarDto implements Serializable {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(min = 3, max = 255, message = "El nombre debe tener entre 3 y 255 caracteres.")
    private String nombre;

    @NotNull(message = "El ID del creador es obligatorio.")
    @Min(value = 1, message = "El ID del creador debe ser un valor positivo.")
    private Integer creadoPor; 

    @NotNull(message = "El tipo de deporte es obligatorio.")
    @Min(value = 1, message = "El tipo de deporte ID debe ser un valor positivo.")
    private Integer tipoDeporteId;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres.")
    private String descripcion;

    @Size(max = 500, message = "La URL del logo no puede exceder los 500 caracteres.")
    private String logo;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "El color principal debe ser un código hexadecimal válido (ej: #FFFFFF).")
    @Size(max = 7, message = "El color principal no debe exceder 7 caracteres (incluyendo #).")
    private String colorPrincipal;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "El color secundario debe ser un código hexadecimal válido (ej: #FFFFFF).")
    @Size(max = 7, message = "El color secundario no debe exceder 7 caracteres (incluyendo #).")
    private String colorSecundario;

    @Size(max = 100, message = "La ciudad no puede exceder los 100 caracteres.")
    private String ciudad;

    @Pattern(regexp = "principiante|intermedio|avanzado|profesional", message = "Nivel inválido. Debe ser: principiante, intermedio, avanzado o profesional.")
    private String nivel;

    @Min(value = 5, message = "El mínimo de miembros es 5.")
    @Max(value = 50, message = "El máximo de miembros es 50.")
    private Integer maxMiembros = 15;

    @NotNull(message = "El requerimiento de aprobación es obligatorio.")
    private Boolean requiereAprobacion = true;

    @NotNull(message = "El estado activo es obligatorio.")
    private Boolean estaActivo = true;
}