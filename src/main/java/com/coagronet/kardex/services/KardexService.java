package com.coagronet.kardex.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KardexService {

	private final KardexRepository kardexRepository;

	private final KardexMapper kardexMapper;

	private final EstadoRepository estadoRepository;

	private final UserEmpresaService userEmpresaService;

	private final EmpresaRepository empresaRepository;

	public Page<KardexDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return kardexRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(kardexMapper::toDto);
	}

	public Optional<KardexDTO> findById(Long requestedId) {
		return kardexRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(kardexMapper::toDto);
	}

	@Transactional
	public KardexDTO create(KardexDTO kardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		estadoRepository.findById(kardexDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		kardexDTO.setEmpresaId(empresaId);


		Empresa empresa = empresaRepository.findById(empresaId)
				.orElseThrow(() -> new BadRequestException("Empresa no encontrada"));


		Kardex kardex = kardexMapper.toEntity(kardexDTO);
		kardex.setEmpresa(empresa);

		if (kardexDTO.getClienteProveedorId() != null) {
			Empresa clienteProveedor = empresaRepository.findById(kardexDTO.getClienteProveedorId())
					.orElseThrow(() -> new BadRequestException("Cliente/Proveedor no encontrado"));
			kardex.setClienteProveedor(clienteProveedor);
		}
		Kardex guardado = kardexRepository.save(kardex);

		return kardexMapper.toDto(guardado);
	}

	@Transactional
	public void update(Long requestedId, KardexDTO kardexDTO) {
		kardexRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Kardex no encontrada o no válida"));

		estadoRepository.findById(kardexDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		kardexDTO.setId(requestedId);
		kardexDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		Kardex kardex = kardexMapper.toEntity(kardexDTO);
		if (kardexDTO.getClienteProveedorId() != null) {
			Empresa clienteProveedor = empresaRepository.findById(kardexDTO.getClienteProveedorId())
					.orElseThrow(() -> new BadRequestException("Cliente/Proveedor no encontrado"));
			kardex.setClienteProveedor(clienteProveedor);
		}
		Kardex guardado = kardexRepository.save(kardex);

		kardexRepository.save(guardado);
	}

	@Transactional
	public void delete(Long requestId) {
		kardexRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Kardex no encontrado o no válido"));

		kardexRepository.deleteById(requestId);
	}

}
