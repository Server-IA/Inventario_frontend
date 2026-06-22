package com.coagronet.validator.inventario.entidades;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlmacenValidator implements BaseValidator {

    private final AlmacenRepository almacenRepository;


    public Almacen validarAlmacen(Long almacenId, Long empresaId) {
        return almacenRepository.findByIdAndEmpresaId(almacenId, empresaId)
                .orElseThrow(() -> new BadRequestException("almacen.not-found", almacenId));
    }
}
