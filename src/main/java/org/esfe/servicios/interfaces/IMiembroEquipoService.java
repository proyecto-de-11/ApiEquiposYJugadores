package org.esfe.servicios.interfaces;

import org.esfe.dtos.miembro.MiembroAprobarDto;
import org.esfe.dtos.miembro.MiembroCrearDto;
import org.esfe.dtos.miembro.MiembroModificarDto;
import org.esfe.dtos.miembro.MiembroSalidaDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IMiembroEquipoService {

    // 1. CREACIÓN / SOLICITUD
    MiembroSalidaDto crearMembresia(MiembroCrearDto miembroCrearDto);

    Optional<MiembroSalidaDto> obtenerPorId(Integer id);

    Page<MiembroSalidaDto> obtenerMiembrosPorEquipo(Integer equipoId, Pageable pageable);

    Page<MiembroSalidaDto> obtenerMembresiasPorUsuario(Integer usuarioId, Pageable pageable);

    // 3. MODIFICACIÓN
    MiembroSalidaDto editarAtributos(MiembroModificarDto miembroModificarDto);

    MiembroSalidaDto gestionarEstado(MiembroAprobarDto miembroAprobarDto);

    // 4. ELIMINACIÓN
    void eliminarMembresia(Integer id);

    // 5. UTILIDAD
    boolean esMiembroExistente(Integer equipoId, Integer usuarioId);
}