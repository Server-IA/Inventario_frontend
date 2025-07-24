package com.coagronet.articuloKardex.controllers;

import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;
import com.coagronet.articuloKardex.services.ArticuloKardexService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ArticuloKardexControllerV2 {

	private final ArticuloKardexService articuloKardexService;

	@GetMapping("/kardex/{kardexId}/articulos")
	public ResponseEntity<List<ArticuloKardexDTO>> findArticulosByKardex(@PathVariable Long kardexId) {
		return ResponseEntity.ok(articuloKardexService.findAllByKardexId(kardexId));
	}

}
