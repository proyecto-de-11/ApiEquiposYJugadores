package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "equipos")
public class Equipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    // FK a la API de Usuarios: el usuario que creó el equipo
    @Column(name = "creado_por", nullable = false)
    private Integer creadoPor;

    @Column(name = "tipo_deporte_id", nullable = false)
    private Integer tipoDeporteId;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "logo", length = 500)
    private String logo;

    @Column(name = "color_principal", length = 7)
    private String colorPrincipal;

    @Column(name = "color_secundario", length = 7)
    private String colorSecundario;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel", columnDefinition = "VARCHAR(20)")
    private NivelEquipo nivel;

    @Column(name = "max_miembros", columnDefinition = "INT DEFAULT 15")
    private Integer maxMiembros = 15;

    @Column(name = "requiere_aprobacion", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean requiereAprobacion = true;

    @Column(name = "calificacion_promedio", precision = 3, scale = 2, columnDefinition = "DECIMAL(3, 2) DEFAULT 0")
    private BigDecimal calificacionPromedio = BigDecimal.ZERO;

    @Column(name = "total_calificaciones", columnDefinition = "INT DEFAULT 0")
    private Integer totalCalificaciones = 0;

    @Column(name = "esta_activo", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean estaActivo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}