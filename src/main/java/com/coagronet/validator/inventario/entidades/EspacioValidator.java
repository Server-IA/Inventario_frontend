package com.coagronet.validator.inventario.entidades;

import com.coagronet.espacio.Espacio;
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.validator.common.BaseValidator;
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
