package com.coagronet.kardex.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
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
		estadoRepository.findById(kardexDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		kardexDTO.setId(null);
		kardexDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return kardexMapper.toDto(kardexRepository.save(kardexMapper.toEntity(kardexDTO)));
	}

	@Transactional
	public void update(Long requestedId, KardexDTO kardexDTO) {
		kardexRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Kardex no encontrada o no válida"));

		estadoRepository.findById(kardexDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		kardexDTO.setId(requestedId);
		kardexDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		kardexRepository.save(kardexMapper.toEntity(kardexDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		kardexRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Kardex no encontrado o no válido"));

		kardexRepository.deleteById(requestId);
	}

}
