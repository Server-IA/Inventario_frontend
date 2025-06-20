package com.coagronet.inventario.services;

import com.coagronet.inventario.repositories.VistaInventarioProductoRepository;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VistaInventarioProductoService {

    private final VistaInventarioProductoRepository vistaInventarioProductoRepository;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;
}
