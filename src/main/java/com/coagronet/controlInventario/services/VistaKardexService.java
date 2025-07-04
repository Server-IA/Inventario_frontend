package com.coagronet.controlInventario.services;

import com.coagronet.controlInventario.VistaKardex;
import com.coagronet.controlInventario.repositories.VistaKardexRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VistaKardexService {

    private final VistaKardexRepository vistaKardexRepository;
    private final UserEmpresaService userEmpresaService;

    public List<VistaKardex> findByProEmpresaId() {
        return vistaKardexRepository.findByEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
    }
}
