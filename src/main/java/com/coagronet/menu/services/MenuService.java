package com.coagronet.menu.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.menu.dtos.MenuModuloResponseDTO;
import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.repositories.MenuModuloRepository;
import com.coagronet.menu.repositories.projections.SubModuloRow;
import com.coagronet.modulo.mappers.ModuloMapper;
import com.coagronet.tipoaplicacion.enums.TipoAplicacionEnum;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.utils.UserRoleService;

import lombok.RequiredArgsConstructor;

/**
 * Servicio de dominio responsable de construir el menú visible para el usuario.
 * <p>
 * Resuelve la empresa y el rol desde el contexto de seguridad, traduce el
 * {@code tipoAplicacion} a {@link TipoAplicacionEnum}, consulta el repositorio
 * y agrupa los módulos por subsistema para producir la estructura final del
 * menú.
 * </p>
 *
 * <p>
 * <strong>Principios:</strong> SRP (construcción del menú), SoC (consulta en
 * repository), y uso de {@link ModuloMapper} para separar el mapeo entidad→DTO.
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@Service
@RequiredArgsConstructor
public class MenuService {

	private final MenuModuloRepository menuModuloRepository;
	private final ModuloMapper moduloMapper;
	private final UserEmpresaService userEmpresaService;
	private final UserRoleService userRoleService;

	/**
	 * Obtiene el menú para la empresa actual y el rol actual del usuario, filtrado
	 * por tipo de aplicación.
	 * <p>
	 * Pasos:
	 * <ol>
	 * <li>Resuelve {@code empresaId} y {@code roleName} del contexto.</li>
	 * <li>Convierte {@code tipoAplicacion} a {@link TipoAplicacionEnum} y obtiene
	 * su ID interno.</li>
	 * <li>Consulta
	 * {@link MenuRepository#findSubmodulosByEmpresaTipoAppAndRol(Long, Integer, String)}.</li>
	 * <li>Agrupa por subsistema (nombre + icono) y mapea cada fila a
	 * {@link MenuModuloResponseDTO}.</li>
	 * </ol>
	 * </p>
	 *
	 * @param tipoAplicacion cadena {@code "web"} o {@code "movil"} (no sensible a
	 *                       mayúsculas)
	 * @return lista de subsistemas, cada uno con sus módulos, en orden estable (por
	 *         nombre de subsistema y módulo)
	 * @throws IllegalArgumentException si {@code tipoAplicacion} no corresponde a
	 *                                  un valor soportado
	 */
	public List<MenuSubSistemaResponseDTO> obtenerMenuPorEmpresaTipoYRol(String tipoAplicacion) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		String roleName = userRoleService.getRoleFromCurrentRequest();

		int tipoAppId = TipoAplicacionEnum.from(tipoAplicacion).id();

		var rows = menuModuloRepository.findSubmodulosByEmpresaTipoAppAndRol(empresaId, tipoAppId, roleName);

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
