package com.inventario.productopresentacionstock.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventario.productopresentacionstock.dtos.ProductoPresentacionStockResponseDTO;
import com.inventario.productopresentacionstock.services.ProductoPresentacionStockService;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ProductoPresentacionStockController {

	private final ProductoPresentacionStockService productoPresentacionStockService;

	private final UserEmpresaService userEmpresaService;

	@GetMapping("api/v1/stock")
	public ResponseEntity<Page<ProductoPresentacionStockResponseDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<ProductoPresentacionStockResponseDTO> page = productoPresentacionStockService.findAll(pageable);

		return ResponseEntity.ok(page);
	}

	@GetMapping("api/v2/stock")
	public ResponseEntity<List<ProductoPresentacionStockResponseDTO>> findStockByAlmacenAndProducto(
			@RequestParam(name = "productoPresentacionId", required = false) Long productoPresentacionId,
			@RequestParam(name = "almacenId", required = false) Long almacenId) {

		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		List<ProductoPresentacionStockResponseDTO> response = productoPresentacionStockService
			.findStockDinamico(productoPresentacionId, almacenId, empresaId);

		return ResponseEntity.ok(response);
	}

}
