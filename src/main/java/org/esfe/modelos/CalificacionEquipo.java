package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "calificaciones_equipo")
public class CalificacionEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación Many-to-One con Equipo (el equipo evaluado)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_evaluado_id", nullable = false)
    private Equipo equipoEvaluado;
    
    // Campo opcional, FK a partidos(id) (gestión externa)
    @Column(name = "partido_id")
    private Integer partidoId; 
    
    // FK a la API de Usuarios: el usuario que realiza la evaluación
    @Column(name = "evaluador_id", nullable = false)
    private Integer evaluadorId; 

    @Column(name = "puntuacion", precision = 2, scale = 1)
    private BigDecimal puntuacion;

    // Los campos JSON se mapean como String/TEXT en JPA
    @Column(name = "aspectos_positivos", columnDefinition = "JSON")
    private String aspectosPositivos;

    @Column(name = "aspectos_mejorar", columnDefinition = "JSON")
    private String aspectosMejorar;

    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "es_anonimo", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean esAnonimo = false;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}