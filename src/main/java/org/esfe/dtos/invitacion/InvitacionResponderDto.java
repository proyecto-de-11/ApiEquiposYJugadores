package org.esfe.dtos.invitacion;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import org.esfe.enums.EstadoInvitacion;

import java.io.Serializable;

@Getter
@Setter
public class InvitacionResponderDto implements Serializable {

    @NotNull(message = "El ID de la invitación es obligatorio")
    private Integer id;

    @NotNull(message = "El nuevo estado es obligatorio")
    private EstadoInvitacion nuevoEstado;
    
    // Este ID debe ser el mismo que el usuario autenticado que responde
    @NotNull(message = "El ID de la persona que responde es obligatorio")
    private Integer usuarioRespondioId; 
}