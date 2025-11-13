package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "equipo_miembros", uniqueConstraints = {
        // Restricción para asegurar que un usuario solo puede ser miembro de un equipo una vez
        @UniqueConstraint(columnNames = {"equipo_id", "usuario_id"}, name = "unique_miembro")
})
public class MiembroEquipo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // FK al Equipo local (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipo_id", nullable = false)
    private Equipo equipo;

    // FK al Usuario (externo): ID del usuario que es miembro del equipo
    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "rol", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'jugador'")
    private String rol = "jugador";

    @Column(name = "numero_camiseta")
    private Integer numeroCamiseta;

    @Column(name = "posicion", length = 100)
    private String posicion;

    // Estado del miembro: 'activo', 'inactivo', 'suspendido'
    @Column(name = "estado", length = 50, columnDefinition = "VARCHAR(50) DEFAULT 'activo'")
    private String estado = "activo";

    @Column(name = "fecha_union", nullable = false, updatable = false)
    private LocalDateTime fechaUnion;

    @PrePersist
    protected void onCreate() {
        this.fechaUnion = LocalDateTime.now();
    }
}