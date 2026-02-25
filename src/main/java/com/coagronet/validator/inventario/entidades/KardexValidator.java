package com.coagronet.validator.inventario.entidades;

import org.springframework.stereotype.Component;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.repositories.KardexRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KardexValidator {

	private final KardexRepository kardexRepository;

	public Kardex obtenerKardexParaActualizar(Long kardexId, Long empresaId) {
		return kardexRepository.findByIdAndEmpresaId(kardexId, empresaId)
			.orElseThrow(() -> new NotFoundException("kardex.not-found", kardexId));
	}

	public void verificarExistenciaKardex(Long kardexId, Long empresaId) {
		if (!kardexRepository.existsByIdAndEmpresaId(kardexId, empresaId)) {
			throw new NotFoundException("kardex.not-found", kardexId);
		}
	}

	public Kardex obtenerReferencia(Long kardexId) {
		return kardexRepository.getReferenceById(kardexId);
	}

}
