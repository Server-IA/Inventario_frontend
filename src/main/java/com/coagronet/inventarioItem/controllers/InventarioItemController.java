package com.coagronet.inventarioItem.controllers;

import com.coagronet.inventarioItem.services.InventarioItemService;
import com.coagronet.inventarioItem.dtos.InventarioItemDTO;
import com.coagronet.utils.UriBuilderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario_item")
@RequiredArgsConstructor
public class InventarioItemController {

	private final InventarioItemService inventarioItemService;

	private final UriBuilderUtil uriBuilderUtil;

	@GetMapping
	public ResponseEntity<List<InventarioItemDTO>> findAll() {
		List<InventarioItemDTO> inventarioItemDTOList = inventarioItemService.findAll();

		return inventarioItemDTOList.isEmpty() ? ResponseEntity.noContent().build()
				: ResponseEntity.ok(inventarioItemDTOList);
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
