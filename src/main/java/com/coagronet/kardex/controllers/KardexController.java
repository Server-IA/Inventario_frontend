package com.coagronet.kardex.controllers;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.coagronet.auditoria.RequestUtils;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.services.KardexService;
import com.coagronet.utils.UriBuilderUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/kardex")
@RequiredArgsConstructor
public class KardexController {

	private final KardexService kardexService;

	private final UriBuilderUtil uriBuilderUtil;

	private final RequestUtils requestUtils;

	@GetMapping
	public ResponseEntity<Page<KardexDTO>> findAll(@PageableDefault Pageable pageable) {
		Page<KardexDTO> page = kardexService.findAll(pageable);

		return ResponseEntity.ok(page);
	}

	@GetMapping("/{requestedId}")
	public ResponseEntity<KardexDTO> findById(@PathVariable Long requestedId) {
		return kardexService.findById(requestedId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<Void> crearKardex(@RequestBody @Valid KardexDTO kardexDTO, HttpServletRequest request,
			UriComponentsBuilder ucb) {

		String ip = requestUtils.getClientIp(request);
		String host = requestUtils.getClientHost(request);

		KardexDTO savedKardexDTO = kardexService.create(kardexDTO, ip, host);

		URI locationOfNewKardex = uriBuilderUtil.buildKardexUri(savedKardexDTO.getId(), ucb);
		return ResponseEntity.created(locationOfNewKardex).build();
	}

	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> actualizarKardex(@PathVariable Long requestedId,
			@RequestBody @Valid KardexDTO kardexDTO, HttpServletRequest request) {

		String ip = requestUtils.getClientIp(request);
		String host = requestUtils.getClientHost(request);

		kardexService.update(requestedId, kardexDTO, ip, host);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{requestedId}")
	public ResponseEntity<Void> eliminarKardex(@PathVariable Long requestedId) {
		kardexService.inactivarMovimiento(requestedId);
		return ResponseEntity.noContent().build();
	}

}