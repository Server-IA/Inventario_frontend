package com.coagronet.controlInventario.services;

import com.coagronet.controlInventario.VistaEmpresaInventario;
import com.coagronet.controlInventario.repositories.VistaEmpresaInventarioRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VistaEmpresaInventarioService {

    private final VistaEmpresaInventarioRepository vistaEmpresaInventarioRepository;
    private final UserEmpresaService userEmpresaService;

    public List<VistaEmpresaInventario> findByInvEmpresaId() {
        return vistaEmpresaInventarioRepository.findByInvEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
    }

    public List<VistaEmpresaInventario> findByEmpresaIdAndSubseccionId(Long subSeccionId) {
        return vistaEmpresaInventarioRepository.findByInvEmpresaIdAndInvSubSeccionId(
                userEmpresaService.getEmpresaIdFromCurrentRequest(), subSeccionId);
    }

}
