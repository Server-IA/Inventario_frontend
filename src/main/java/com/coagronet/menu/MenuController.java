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

@RestController
@RequestMapping("/api/v2/menu")
@RequiredArgsConstructor
public class MenuController {

	private final MenuService menuService;

	@GetMapping
	public ResponseEntity<List<MenuSubSistemaResponseDTO>> listarSubsistemas(@RequestParam String tipoAplicacion) {
		List<MenuSubSistemaResponseDTO> data = menuService.obtenerMenuPorEmpresaTipoYRol(tipoAplicacion);
		return ResponseEntity.ok(data);
	}

}
