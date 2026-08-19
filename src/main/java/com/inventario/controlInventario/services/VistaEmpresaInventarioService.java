package com.inventario.controlInventario.services;

import com.inventario.controlInventario.VistaEmpresaInventario;
import com.inventario.controlInventario.repositories.VistaEmpresaInventarioRepository;
import com.inventario.utils.UserEmpresaService;
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
		return vistaEmpresaInventarioRepository
			.findByInvEmpresaIdAndInvSubSeccionId(userEmpresaService.getEmpresaIdFromCurrentRequest(), subSeccionId);
	}

}
