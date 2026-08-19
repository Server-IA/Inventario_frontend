package com.inventario.facturacion.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.facturacion.dtos.FacturacionDTO;
import com.inventario.facturacion.mappers.FacturacionMapper;
import com.inventario.facturacion.repositories.FacturacionRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacturacionService {

	private final FacturacionRepository facturacionRepository;

	private final FacturacionMapper facturacionMapper;

	private final UserEmpresaService userEmpresaService;

	public Page<FacturacionDTO> findAll(Pageable pageable) {
		return facturacionRepository
			.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable)
			.map(facturacionMapper::toDTO);
	}

	public FacturacionDTO findById(Long requestedId) {
		return facturacionRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(facturacionMapper::toDTO)
			.orElseThrow(() -> new NotFoundException("facturacion.not-found", requestedId));
	}

	@Transactional
	public FacturacionDTO create(FacturacionDTO facturacionDTO) {
		facturacionDTO.setId(null);
		facturacionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());
		return facturacionMapper.toDTO(facturacionRepository.save(facturacionMapper.toEntity(facturacionDTO)));
	}

	@Transactional
	public void update(Long requestedId, FacturacionDTO facturacionDTO) {
		var empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		facturacionRepository.findByIdAndEmpresaId(requestedId, empresaId)
			.orElseThrow(() -> new NotFoundException("facturacion.not-found", requestedId));

		facturacionDTO.setId(requestedId);
		facturacionDTO.setEmpresaId(empresaId);

		facturacionRepository.save(facturacionMapper.toEntity(facturacionDTO));
	}

	@Transactional
	public void delete(Long requestedId) {
		facturacionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("facturacion.not-found", requestedId));

		facturacionRepository.deleteById(requestedId);
	}

}
