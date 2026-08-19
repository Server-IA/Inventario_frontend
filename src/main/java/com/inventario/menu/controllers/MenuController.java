package com.inventario.menu.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.menu.dtos.AsignarModulosRequestDTO;
import com.inventario.menu.dtos.MenuSubSistemaResponseDTO;
import com.inventario.menu.services.MenuService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para exponer el menú de la aplicación.
 * <p>
 * Entrega la estructura del menú agrupada por subsistemas para la empresa y rol del usuario autenticado, filtrando
 * además por tipo de aplicación (WEB o MOVIL). La lógica de armado del menú se delega al {@link MenuService}.
 * </p>
 *
 * <p>
 * <strong>Ruta base:</strong> {@code /api/v2/menu}
 * </p>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */
@RestController @RequestMapping("/api/v2/menu") @RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	/**
	 * Lista los subsistemas y sus módulos visibles para la empresa y rol actuales, filtrando por tipo de aplicación.
	 *
	 * @param tipoAplicacion tipo de aplicación a filtrar (valores soportados: {@code web} | {@code movil})
	 * @return respuesta 200 con una lista de {@link MenuSubSistemaResponseDTO}
	 * @throws IllegalArgumentException si el parámetro {@code tipoAplicacion} no es válido
	 */
	@GetMapping
	public ResponseEntity<List<MenuSubSistemaResponseDTO>> listarSubsistemas(@RequestParam String tipoAplicacion) {
		List<MenuSubSistemaResponseDTO> data = menuService.obtenerMenuPorEmpresaTipoYRol(tipoAplicacion);
		return ResponseEntity.ok(data);
	}

	@PostMapping("modulos")
	public ResponseEntity<Map<String, String>> asignarModulos(@RequestBody AsignarModulosRequestDTO request) {

		menuService.asignarModulosAEmpresa(request.getModulosIds());

		Map<String, String> response = new HashMap<>();
		response.put("mensaje", "Módulos procesados correctamente");

		return ResponseEntity.ok(response);
	}

}
