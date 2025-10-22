package com.coagronet.menu.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.repositories.MenuRepository;
import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.mappers.ModuloMapper;
import com.coagronet.tipoaplicacion.enums.TipoAplicacionEnum;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.utils.UserRoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuRepository menuRepository;
	private final ModuloMapper moduloMapper;
	private final UserEmpresaService userEmpresaService;
	private final UserRoleService userRoleService;

	public List<MenuSubSistemaResponseDTO> obtenerMenuPorEmpresaTipoYRol(String tipoAplicacion) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		String roleName = userRoleService.getRoleFromCurrentRequest();

		int tipoAppId = TipoAplicacionEnum.from(tipoAplicacion).id();

		var rows = menuRepository.findSubmodulosByEmpresaTipoAppAndRol(empresaId, tipoAppId, roleName);

		Map<String, List<SubModuloRow>> agrupado = rows.stream().collect(Collectors
				.groupingBy(r -> r.getSubNombre() + "||" + r.getSubIcon(), LinkedHashMap::new, Collectors.toList()));

		List<MenuSubSistemaResponseDTO> out = new ArrayList<>();
		for (var e : agrupado.entrySet()) {
			String[] parts = e.getKey().split("\\|\\|", 2);
			String subNombre = parts[0];
			String subIcon = parts.length > 1 ? parts[1] : null;

			List<MenuModuloResponseDTO> modulos = e.getValue().stream().map(moduloMapper::toDTO).toList();

			out.add(MenuSubSistemaResponseDTO.builder().nombre(subNombre).icono(subIcon).modulos(modulos).build());
		}
		return out;
	}

}
