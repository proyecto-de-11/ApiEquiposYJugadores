package org.esfe.dtos.invitacion;

import lombok.Getter;
import lombok.Setter;
import org.esfe.dtos.equipo.EquipoReferenciaDto;
import org.esfe.enums.EstadoInvitacion;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class InvitacionSalidaDto implements Serializable {
    private Integer id;
    
    private Integer usuarioInvitadoId;
    private Integer usuarioRemitenteId;
    private Integer usuarioRespondioId;

    private String mensaje;
    private EstadoInvitacion estado;
    private LocalDateTime fechaRespuesta;
    private LocalDateTime fechaCreacion;

    // Objeto anidado para la referencia del Equipo
    private EquipoReferenciaDto equipo;
}