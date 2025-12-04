package com.coagronet.validator.seguridad;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.rol.Rol;
import com.coagronet.rol.repositories.RolRepository;
import com.coagronet.validator.common.BaseValidator;
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
