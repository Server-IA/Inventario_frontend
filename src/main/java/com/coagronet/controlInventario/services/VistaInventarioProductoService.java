package com.coagronet.controlInventario.services;

import com.coagronet.controlInventario.VistaInventarioProducto;
import com.coagronet.controlInventario.repositories.VistaInventarioProductoRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VistaInventarioProductoService {

	private final VistaInventarioProductoRepository vistaInventarioProductoRepository;

	private final UserEmpresaService userEmpresaService;

	public List<VistaInventarioProducto> findByProEmpresaId() {
		return vistaInventarioProductoRepository
			.findByProEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
	}

}
