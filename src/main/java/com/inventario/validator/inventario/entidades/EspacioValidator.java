package com.inventario.validator.inventario.entidades;

import com.inventario.espacio.Espacio;
import com.inventario.espacio.repositories.EspacioRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EspacioValidator implements BaseValidator {
    private final EspacioRepository espacioRepository;


    public Espacio validarEspacio(Long espacioId, Long empresaId){
        return espacioRepository.findByIdAndEmpresaId(espacioId, empresaId)
                .orElseThrow(()-> new NotFoundException("espacio.not-found", espacioId));
    }
}
