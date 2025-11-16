package com.coagronet.validator.inventario.entidades;

import com.coagronet.cierreinventario.repositories.CierreInventarioRepository;
import com.coagronet.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CierreInventarioValidator implements BaseValidator {

    private final CierreInventarioRepository cierreInventarioRepository;

    public void validarDuplicado(Long empresaId, Long almacenId, LocalDate fechaInicio, LocalDate fechaCorte) {

        boolean existe = cierreInventarioRepository.existeCierreEnMes(
                empresaId,
                almacenId,
                fechaInicio,
                fechaCorte
        );

        if (existe) {
            throw new IllegalStateException(
                    "Ya existe un cierre de inventario para el almacén " + almacenId +
                            " en el rango " + fechaInicio + " - " + fechaCorte
            );
        }
    }

}
