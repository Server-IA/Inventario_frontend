package com.coagronet.productopresentacionstock.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.productopresentacionstock.dtos.ProductoPresentacionStockResponseDTO;
import com.coagronet.productopresentacionstock.mappers.ProductoPresentacionStockMapper;
import com.coagronet.productopresentacionstock.repositories.ProductoPresentacionStockRepository;
import com.coagronet.productopresentacionstock.specs.ProductoPresentacionStockSpecs;
import com.coagronet.utils.UserEmpresaService;

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
