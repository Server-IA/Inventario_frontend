package com.coagronet.ordenCompra.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;
import com.coagronet.ordenCompra.mappers.OrdenCompraMapper;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

	private final OrdenCompraRepository ordenCompraRepository;

	private final OrdenCompraMapper ordenCompraMapper;

	private final UserEmpresaService userEmpresaService;

	public Page<OrdenCompraDTO> findAll(Pageable pageable) {
		return ordenCompraRepository
			.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable)
			.map(ordenCompraMapper::toDTO);
	}

	public OrdenCompraDTO findById(Long requestedId) {
		return ordenCompraRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(ordenCompraMapper::toDTO)
			.orElseThrow(() -> new NotFoundException("orden-compra.not-found", requestedId));
	}

	@Transactional
	public OrdenCompraDTO create(OrdenCompraDTO ordenCompraDTO) {
		ordenCompraDTO.setId(null);
		ordenCompraDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
		return ordenCompraMapper.toDTO(ordenCompraRepository.save(ordenCompraMapper.toEntity(ordenCompraDTO)));
	}

	@Transactional
	public void update(Long requestedId, OrdenCompraDTO ordenCompraDTO) {
		var empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		ordenCompraRepository.findByIdAndEmpresaId(requestedId, empresaId)
			.orElseThrow(() -> new NotFoundException("orden-compra.not-found", requestedId));

		ordenCompraDTO.setId(requestedId);
		ordenCompraDTO.setEmpresaId(empresaId);

		ordenCompraRepository.save(ordenCompraMapper.toEntity(ordenCompraDTO));
	}

	@Transactional
	public void delete(Long requestedId) {
		ordenCompraRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("orden-compra.not-found", requestedId));

		ordenCompraRepository.deleteById(requestedId);
	}

}
