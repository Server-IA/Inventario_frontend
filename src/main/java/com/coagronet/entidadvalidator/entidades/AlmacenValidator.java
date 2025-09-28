package com.coagronet.entidadvalidator.entidades;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.entidadvalidator.constantes.MensajesValidaciones;
import com.coagronet.exceptionHandler.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlmacenValidator {

    private final AlmacenRepository almacenRepository;


    public Almacen validarAlmacen(Long almacenId, Long empresaId) {
        return almacenRepository.findByIdAndEmpresaId(almacenId, empresaId)
                .orElseThrow(() -> new BadRequestException(MensajesValidaciones.ALMACEN_NO_VALIDO));
    }
}
