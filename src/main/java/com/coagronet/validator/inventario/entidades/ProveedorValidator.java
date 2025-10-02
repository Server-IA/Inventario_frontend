package com.coagronet.validator.inventario.entidades;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.proveedor.repositories.ProveedorRepository;
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
