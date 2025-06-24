package com.coagronet.controlInventario.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.controlInventario.VistaInventarioProducto;
import com.coagronet.controlInventario.repositories.VistaInventarioProductoRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VistaInventarioProductoService {

    private final VistaInventarioProductoRepository vistaInventarioProductoRepository;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;


    public List<VistaInventarioProducto> findByProEmpresaId() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return vistaInventarioProductoRepository.findByProEmpresaId(empresaId);
    }
}
