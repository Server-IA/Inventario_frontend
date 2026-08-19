package com.inventario.estado.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.estado.Estado;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.pedido.estados.PedidoEstadoConst;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EstadoResolver {

	private final EstadoRepository estadoRepository;

	private final Map<String, Long> cache = new ConcurrentHashMap<>();

	private static String key(long categoriaId, String acronimo) {
		return categoriaId + "::" + (acronimo == null ? "" : acronimo.toUpperCase());
	}

	@Transactional(readOnly = true)
	public Long idByCategoriaIdYAcronimo(long categoriaId, String acronimo) {
		if (categoriaId <= 0) {
			throw new IllegalArgumentException("categoria.not-valid" + categoriaId);
		}
		if (acronimo == null || acronimo.trim().isEmpty()) {
			throw new IllegalArgumentException("validation.acronimo.not-blank");
		}

		final String acr = acronimo.trim().toUpperCase();
		final String k = key(categoriaId, acr);

		return cache.computeIfAbsent(k, __ -> estadoRepository.findIdByCategoriaIdAndAcronimo(categoriaId, acr)
			.orElseThrow(
					() -> new NotFoundException("estado.not-found.by-acronimo", new Object[] { categoriaId, acr })));
	}

	@Transactional(readOnly = true)
	public Long idPedido(String acronimo) {
		return idByCategoriaIdYAcronimo(PedidoEstadoConst.CATEGORIA_ID, acronimo);
	}

	@Transactional(readOnly = true)
	public Estado getEntity(Long id) {
		return estadoRepository.findById(id).orElseThrow(() -> new NotFoundException("estado.not-found", id));
	}

	public void clearCache() {
		cache.clear();
	}

	@Transactional(readOnly = true)
	public Estado getRef(Long id) {
		return estadoRepository.getReferenceById(id);
	}

}
