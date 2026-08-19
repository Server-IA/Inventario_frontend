package com.inventario.validator.inventario.entidades;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.proveedor.Proveedor;
import com.inventario.proveedor.repositories.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProveedorValidator {

    private final ProveedorRepository proveedorRepository;

    public Proveedor validarProveedor(Long proveedorId, Long empresaId){
        return proveedorRepository.findByIdAndEmpresaId(proveedorId, empresaId)
                .orElseThrow(()-> new NotFoundException("proveedor.not-found", proveedorId));
    }
}
