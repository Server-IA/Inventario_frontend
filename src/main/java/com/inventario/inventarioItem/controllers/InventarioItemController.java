package com.inventario.inventarioItem.controllers;

import com.inventario.inventarioItem.services.InventarioItemService;
import com.inventario.inventarioItem.dtos.InventarioItemDTO;
import com.inventario.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/inventario_item")
@RequiredArgsConstructor
public class InventarioItemController {

	private final InventarioItemService inventarioItemService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<Page<InventarioItemDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<InventarioItemDTO> page = inventarioItemService.findAll(pageable);

		if(page.isEmpty()){
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<InventarioItemDTO> findById(@PathVariable Long requestedId) {
		return inventarioItemService.findById(requestedId)
			.map(ResponseEntity::ok)
			.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<InventarioItemDTO> createInventarioItem(
			@RequestBody @Valid InventarioItemDTO newInventarioItem, UriComponentsBuilder ucb) {

		InventarioItemDTO savedInventarioItemDTO = inventarioItemService.create(newInventarioItem);

		URI locationOfNewInventarioItem = uriBuilderUtil.buildInventarioItemUri(savedInventarioItemDTO.getId(), ucb);

		return ResponseEntity.created(locationOfNewInventarioItem).build();

	}

	@PutMapping("/{requestedId}")
	private ResponseEntity<Void> putInventarioItem(@PathVariable Long requestedId,
			@RequestBody @Valid InventarioItemDTO inventarioItemDTOUpdate) {
		inventarioItemService.update(requestedId, inventarioItemDTOUpdate);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{id}")
	private ResponseEntity<Void> deleteInventarioItem(@PathVariable Long id) {

		inventarioItemService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
