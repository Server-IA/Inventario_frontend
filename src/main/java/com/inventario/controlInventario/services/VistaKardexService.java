package com.inventario.controlInventario.services;

import com.inventario.controlInventario.VistaKardex;
import com.inventario.controlInventario.repositories.VistaKardexRepository;
import com.inventario.utils.UserEmpresaService;
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
