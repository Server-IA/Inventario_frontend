package com.coagronet.entidadvalidator.entidades;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.entidadvalidator.constantes.MensajesValidaciones;
import com.coagronet.exceptionHandler.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpresaValidator {

    private final EmpresaRepository empresaRepository;

    public Empresa validarEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new BadRequestException(MensajesValidaciones.EMPRESA_NO_ENCONTRADA));
    }

    public Empresa validarClienteProveedor(Long clienteProveedorId) {
        return empresaRepository.findById(clienteProveedorId)
                .orElseThrow(() -> new BadRequestException(MensajesValidaciones.CLIENTE_PROVEEDOR_NO_VALIDO));
    }
}
