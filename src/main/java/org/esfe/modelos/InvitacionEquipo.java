package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.esfe.enums.EstadoInvitacion;

@Getter
@Setter
@Entity
@Table(name = "invitaciones_equipo")
public class InvitacionEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación ManyToOne con la entidad Equipo (FK: equipo_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    // FK a Usuarios: el usuario que recibe la invitación
    @Column(name = "usuario_invitado_id", nullable = false)
    private Integer usuarioInvitadoId;

    // FK a Usuarios: el usuario que envió la invitación (ej. Capitán)
    @Column(name = "usuario_remitente_id", nullable = false)
    private Integer usuarioRemitenteId;

    // FK a Usuarios: el usuario que procesó la respuesta (puede ser nulo si está pendiente)
    @Column(name = "usuario_respondio_id")
    private Integer usuarioRespondioId;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Enumerated(EnumType.STRING) // IMPORTANTE: Guarda la cadena de texto de la constante
    @Column(name = "estado", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'PENDIENTE'") // Ajustar el DEFAULT
    private EstadoInvitacion estado = EstadoInvitacion.PENDIENTE;

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
    }
}