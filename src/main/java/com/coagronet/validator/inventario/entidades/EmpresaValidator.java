package com.coagronet.validator.inventario.entidades;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmpresaValidator implements BaseValidator {

    private final EmpresaRepository empresaRepository;

    public Empresa validarEmpresa(Long empresaId) {
        return empresaRepository.findById(empresaId)
                .orElseThrow(() -> new NotFoundException("empresa.not-found"));
    }

    public Empresa validarClienteProveedor(Long clienteProveedorId) {
        return empresaRepository.findById(clienteProveedorId)
                .orElseThrow(() -> new NotFoundException("cliente-proveedor.not-found"));
    }
}
