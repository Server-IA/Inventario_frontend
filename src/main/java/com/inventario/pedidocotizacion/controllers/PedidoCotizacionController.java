package com.inventario.pedidocotizacion.controllers;

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

import com.inventario.pedidocotizacion.dtos.PedidoCotizacionRequestDTO;
import com.inventario.pedidocotizacion.dtos.PedidoCotizacionResponseDTO;
import com.inventario.pedidocotizacion.services.PedidoCotizacionService;
import com.inventario.utils.UriBuilderUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para el recurso de cotizaciones de pedidos.
 *
 * <p>
 * Expone endpoints CRUD y de consulta para administrar cotizaciones,
 * delegando la lógica de negocio al
 * {@link com.inventario.pedidocotizacion.services.PedidoCotizacionService}.
 * </p>
 *
 * <h2>Rutas típicas</h2>
 * <ul>
 * <li>{@code GET /api/v1/pedido-cotizacion}: lista paginada de cotizaciones de
 * la empresa actual.</li>
 * <li>{@code GET /api/v1/pedido-cotizacion/{id}}: detalle por
 * identificador.</li>
 * <li>{@code GET /api/v1/pedido-cotizacion/pedido/{pedidoId}}: cotizaciones por
 * pedido.</li>
 * <li>{@code POST /api/v1/pedido-cotizacion}: crea una nueva cotización;
 * retorna {@code 201 Created} con {@code Location} del recurso.</li>
 * <li>{@code PUT /api/v1/pedido-cotizacion/{id}}: actualiza datos de una
 * cotización existente.</li>
 * <li>{@code DELETE /api/v1/pedido-cotizacion/{id}}: elimina una cotización
 * existente.</li>
 * </ul>
 *
 * <p>
 * Las respuestas de error siguen las convenciones del manejador global
 * (por ejemplo, {@code NotFoundException} -> {@code 404}).
 * </p>
 *
 * @author Juan J.
 * @since 0.3.1
 */
@RestController
@RequestMapping("/api/v1/pedido-cotizacion")
@RequiredArgsConstructor
public class PedidoCotizacionController {

	private final PedidoCotizacionService pedidoCotizacionService;

	private final UriBuilderUtil uriBuilderUtil;

	/**
	 * Lista paginada de cotizaciones pertenecientes a la empresa autenticada.
	 *
	 * @param pageable parámetros de paginación (tamaño, página, orden)
	 * @return página de {@code PedidoCotizacionResponseDTO}
	 */
	@GetMapping
	public ResponseEntity<Page<PedidoCotizacionResponseDTO>> findAll(@PageableDefault Pageable pageable) {
		return ResponseEntity.ok(pedidoCotizacionService.findAll(pageable));
	}

	/**
	 * Obtiene el detalle de una cotización por su ID.
	 *
	 * @param id identificador de la cotización
	 * @return DTO de respuesta
	 */
	@GetMapping("/{requestedId}")
	public ResponseEntity<PedidoCotizacionResponseDTO> findById(@PathVariable Long requestedId) {
		return ResponseEntity.ok(pedidoCotizacionService.findById(requestedId));
	}

	/**
	 * Crea una nueva cotización de pedido.
	 *
	 * <p>
	 * Devuelve {@code 201 Created} y establece el encabezado {@code Location} con
	 * la URL del nuevo recurso.
	 * </p>
	 *
	 * @param request    DTO de creación/actualización
	 * @param uriBuilder utilitario para construir la URI del recurso creado
	 * @return respuesta vacía con encabezados de localización
	 */
	@PostMapping
	public ResponseEntity<Void> createPedidoCotizacion(
			@Valid @RequestBody PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO, UriComponentsBuilder ucb) {
		return ResponseEntity
				.created(uriBuilderUtil
						.buildPedidoCotizacionUri((pedidoCotizacionService.create(pedidoCotizacionRequestDTO)).id(),
								ucb))
				.build();
	}

	/**
	 * Actualiza una cotización existente.
	 *
	 * @param requestedId identificador de la cotización a actualizar
	 * @param request     DTO con los nuevos valores
	 * @return {@code 204 No Content} si la actualización fue exitosa
	 */
	@PutMapping("/{requestedId}")
	public ResponseEntity<Void> updatePedidoCotizacion(@PathVariable Long requestedId,
			@Valid @RequestBody PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO) {
		pedidoCotizacionService.update(requestedId, pedidoCotizacionRequestDTO);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Elimina una cotización existente por su ID.
	 *
	 * @param id identificador de la cotización
	 * @return {@code 204 No Content} si la eliminación fue exitosa
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePedidoCotizacion(@PathVariable Long id) {
		pedidoCotizacionService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
