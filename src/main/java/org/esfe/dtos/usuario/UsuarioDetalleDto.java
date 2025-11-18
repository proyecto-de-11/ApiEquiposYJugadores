package org.esfe.dtos.usuario;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class UsuarioDetalleDto implements Serializable {
    private Integer id;
    private String nombreCompleto; // Este es el campo clave
    private String email;
    // Puedes incluir otros campos que necesites, como 'ciudad' o 'fotoPerfil'
}