package com.inventario.validator.parametrizacion.entidades;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.subseccion.Subseccion;
import com.inventario.subseccion.repositories.SubseccionRepository;
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
