package org.esfe.configuracion;

import org.esfe.dtos.miembro.MiembroCrearDto;
import org.esfe.modelos.MiembroEquipo;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // 1. Configuración de estrategia (OK)
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        
        // 2. COMBINAR TODAS LAS REGLAS DE MAPEO EXPLÍCITAS EN UNA SOLA LLAMADA
        modelMapper.createTypeMap(MiembroCrearDto.class, MiembroEquipo.class)
            .addMappings(mapper -> {
                // Regla 1: Omitir el ID (Resuelve el error original de ambigüedad)
                mapper.skip(MiembroEquipo::setId); 
                
                // Regla 2: Omitir la entidad Equipo (Ya que se busca y se setea manualmente en el servicio)
                mapper.skip(MiembroEquipo::setEquipo); 
                
            });

        return modelMapper;
    }
}