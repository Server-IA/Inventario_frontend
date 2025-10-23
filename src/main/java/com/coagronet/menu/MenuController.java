package com.coagronet.menu;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coagronet.menu.dtos.MenuSubSistemaResponseDTO;
import com.coagronet.menu.services.MenuService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para exponer el menú de la aplicación.
 * <p>
 * Entrega la estructura del menú agrupada por subsistemas para la empresa y rol
 * del usuario autenticado, filtrando además por tipo de aplicación (WEB o
 * MOVIL). La lógica de armado del menú se delega al {@link MenuService}.
 * </p>
 *
 * <p>
 * <strong>Ruta base:</strong> {@code /api/v2/menu}
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@RestController
@RequestMapping("/api/v2/menu")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	/**
	 * Lista los subsistemas y sus módulos visibles para la empresa y rol actuales,
	 * filtrando por tipo de aplicación.
	 *
	 * @param tipoAplicacion tipo de aplicación a filtrar (valores soportados:
	 *                       {@code web} | {@code movil})
	 * @return respuesta 200 con una lista de {@link MenuSubSistemaResponseDTO}
	 * @throws IllegalArgumentException si el parámetro {@code tipoAplicacion} no es
	 *                                  válido
	 */
	@GetMapping
	public ResponseEntity<List<MenuSubSistemaResponseDTO>> listarSubsistemas(@RequestParam String tipoAplicacion) {
		List<MenuSubSistemaResponseDTO> data = menuService.obtenerMenuPorEmpresaTipoYRol(tipoAplicacion);
		return ResponseEntity.ok(data);
	}

}
