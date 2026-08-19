package com.inventario.productopresentacionstock.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.productopresentacionstock.dtos.ProductoPresentacionStockResponseDTO;
import com.inventario.productopresentacionstock.mappers.ProductoPresentacionStockMapper;
import com.inventario.productopresentacionstock.repositories.ProductoPresentacionStockRepository;
import com.inventario.productopresentacionstock.specs.ProductoPresentacionStockSpecs;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductoPresentacionStockService {

	private final ProductoPresentacionStockRepository productoPresentacionStockRepository;

	private final UserEmpresaService userEmpresaService;

	private final ProductoPresentacionStockMapper productoPresentacionStockMapper;

	public Page<ProductoPresentacionStockResponseDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		return productoPresentacionStockRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(productoPresentacionStockMapper::toResponseDTO);
	}

	@Transactional(readOnly = true)
	public List<ProductoPresentacionStockResponseDTO> findStockDinamico(Long productoPresentacionId, Long almacenId,
			Long empresaId) {
		var spec = ProductoPresentacionStockSpecs.conFiltrosDinamicos(empresaId, almacenId, productoPresentacionId);

		return productoPresentacionStockRepository.findAll(spec)
			.stream()
			.map(productoPresentacionStockMapper::toResponseDTO)
			.toList();
	}

}
