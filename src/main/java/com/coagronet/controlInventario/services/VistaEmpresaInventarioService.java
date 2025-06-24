package com.coagronet.controlInventario.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.controlInventario.VistaEmpresaInventario;
import com.coagronet.controlInventario.repositories.VistaEmpresaInventarioRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VistaEmpresaInventarioService {

    private final VistaEmpresaInventarioRepository vistaEmpresaInventarioRepository;
    private final UserEmpresaService userEmpresaService;
    private final AuthenticationService authenticationService;

    public List<VistaEmpresaInventario> findByInvEmpresaId() {
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return vistaEmpresaInventarioRepository.findByInvEmpresaId(empresaId);
    }


    public List<VistaEmpresaInventario> findByEmpresaIdAndSubseccionId(Long subSeccionId){
        User user = authenticationService.getAuthenticatedUser();
        Empresa empresa = userEmpresaService.getEmpresaFromUser(user);
        Long empresaId = empresa.getId();

        return vistaEmpresaInventarioRepository.findByInvEmpresaIdAndInvSubSeccionId(empresaId, subSeccionId);
    }

}
