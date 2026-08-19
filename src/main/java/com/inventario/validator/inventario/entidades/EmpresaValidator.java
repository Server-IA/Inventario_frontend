package com.inventario.validator.inventario.entidades;

import com.inventario.empresa.Empresa;
import com.inventario.empresa.repositories.EmpresaRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.validator.common.BaseValidator;
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
