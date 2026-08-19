package com.inventario.validator.inventario.entidades;

import com.inventario.almacen.Almacen;
import com.inventario.almacen.repositories.AlmacenRepository;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlmacenValidator implements BaseValidator {

    private final AlmacenRepository almacenRepository;

    public Almacen validarAlmacen(Long almacenId, Long empresaId) {
        return almacenRepository.findByIdAndEmpresaId(almacenId, empresaId)
                .orElseThrow(() -> new BadRequestException("almacen.not-found: " + almacenId));
    }
}
