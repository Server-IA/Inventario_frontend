package com.inventario.validator.seguridad;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.rol.Rol;
import com.inventario.rol.repositories.RolRepository;
import com.inventario.validator.common.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RolValidator implements BaseValidator {

    private final RolRepository rolRepository;

    public Rol validarRol(Long id){
        return rolRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("El rol no fue encontrado"));
    }
}
