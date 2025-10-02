package com.coagronet.validator.parametrizacion.entidades;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.subseccion.repositories.SubseccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubseccionValidator {
    private final SubseccionRepository subseccionRepository;


    public Subseccion validarSubseccion(Long subseccionId, Long empresaId){
        return subseccionRepository
                .findByIdAndEmpresaId(subseccionId, empresaId)
                .orElseThrow(() -> new NotFoundException("subseccion.not-found", empresaId));
    }
}
