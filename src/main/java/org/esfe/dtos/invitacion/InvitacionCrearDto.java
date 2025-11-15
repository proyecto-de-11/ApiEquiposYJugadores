package org.esfe.dtos.invitacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class InvitacionCrearDto implements Serializable {

    @NotNull(message = "El ID del equipo es obligatorio")
    private Integer equipoId;

    @NotNull(message = "El ID del usuario invitado es obligatorio")
    private Integer usuarioInvitadoId;

    // NOTA: Este campo se debería obtener del contexto de seguridad, no del body.
    @NotNull(message = "El ID del remitente es obligatorio")
    private Integer usuarioRemitenteId; 

    private String mensaje;
}