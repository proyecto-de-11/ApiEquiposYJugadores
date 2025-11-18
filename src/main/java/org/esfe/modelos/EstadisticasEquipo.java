package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "estadisticas_equipo")
public class EstadisticasEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Relación OneToOne con la entidad Equipo.
    // team_id es la clave primaria y clave foránea.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false, unique = true)
    private Equipo equipo;

    @Column(name = "partidos_jugados_total", nullable = false)
    private Integer partidosJugadosTotal = 0;

    @Column(name = "partidos_ganados_total", nullable = false)
    private Integer partidosGanadosTotal = 0;

    @Column(name = "partidos_perdidos_total", nullable = false)
    private Integer partidosPerdidosTotal = 0;

    @Column(name = "partidos_empatados_total", nullable = false)
    private Integer partidosEmpatadosTotal = 0;

    @Column(name = "goles_favor_total", nullable = false)
    private Integer golesFavorTotal = 0;

    @Column(name = "goles_contra_total", nullable = false)
    private Integer golesContraTotal = 0;

    @Column(name = "partidos_jugados_torneos", nullable = false)
    private Integer partidosJugadosTorneos = 0;

    @Column(name = "partidos_ganados_torneos", nullable = false)
    private Integer partidosGanadosTorneos = 0;

    @Column(name = "partidos_perdidos_torneos", nullable = false)
    private Integer partidosPerdidosTorneos = 0;

    @Column(name = "partidos_empatados_torneos", nullable = false)
    private Integer partidosEmpatadosTorneos = 0;

    @Column(name = "goles_favor_torneos", nullable = false)
    private Integer golesFavorTorneos = 0;

    @Column(name = "goles_contra_torneos", nullable = false)
    private Integer golesContraTorneos = 0;

    @Column(name = "torneos_ganados", nullable = false)
    private Integer torneosGanados = 0;

    // Campo de fecha de actualización, mapeado al comportamiento de la base de datos (CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}