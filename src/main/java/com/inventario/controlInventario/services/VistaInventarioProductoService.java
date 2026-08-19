package com.inventario.controlInventario.services;

import com.inventario.controlInventario.VistaInventarioProducto;
import com.inventario.controlInventario.repositories.VistaInventarioProductoRepository;
import com.inventario.utils.UserEmpresaService;
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
