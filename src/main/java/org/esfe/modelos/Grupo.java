package org.esfe.modelos;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grupo_id")
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    // Relación a la API Autenticación y Usuarios
    @Column(name = "lider_usuario_id", nullable = false)
    private Long liderUsuarioId;

    // Clave Foránea (FK) al ID del Patrocinador. Puede ser nulo.
    @Column(name = "patrocinador_id")
    private Long patrocinadorId;

    // Se establece automáticamente al crear.
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Método de pre-persistencia de JPA para asegurar que la fecha de creación se establezca.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
