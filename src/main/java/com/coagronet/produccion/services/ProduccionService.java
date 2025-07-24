package com.coagronet.produccion.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.produccion.dtos.ProduccionDTO;
import com.coagronet.produccion.mappers.ProduccionMapper;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.subseccion.repositories.SubseccionRepository;
import com.coagronet.tipoProduccion.repositories.TipoProduccionRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProduccionService {

	private final ProduccionRepository produccionRepository;

	private final TipoProduccionRepository tipoProduccionRepository;

	private final EspacioRepository espacioRepository;

	private final SubseccionRepository subseccionRepository;

	private final ProduccionMapper produccionMapper;

	private final EstadoRepository estadoRepository;

	private final UserEmpresaService userEmpresaService;

	/* ---------- READ ---------- */

	public List<ProduccionDTO> findAll() {
		return produccionRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(produccionMapper::toDto)
			.collect(Collectors.toList());
	}

	public Optional<ProduccionDTO> findById(Long requestedId) {
		return produccionRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(produccionMapper::toDto);
	}

	/* ---------- CREATE ---------- */

	@Transactional
	public ProduccionDTO create(ProduccionDTO produccionDTO) {

		tipoProduccionRepository
			.findByIdAndEmpresaId(produccionDTO.getTipoProduccionId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("TipoProduccionId not found"));

		espacioRepository
			.findByIdAndEmpresaId(produccionDTO.getEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("EspacioId not found"));

		subseccionRepository
			.findByIdAndEmpresaId(produccionDTO.getSubSeccionId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("SubSeccionId not found"));

		estadoRepository.findById(produccionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("EstadoId not found"));

		produccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return produccionMapper.toDto(produccionRepository.save(produccionMapper.toEntity(produccionDTO)));
	}

	/* ---------- UPDATE ---------- */

	@Transactional
	public void update(Long requestedId, ProduccionDTO produccionDTO) {

		produccionRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Produccion not found"));

		tipoProduccionRepository
			.findByIdAndEmpresaId(produccionDTO.getTipoProduccionId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("TipoProduccionId not found"));

		espacioRepository
			.findByIdAndEmpresaId(produccionDTO.getEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("EspacioId not found"));

		subseccionRepository
			.findByIdAndEmpresaId(produccionDTO.getSubSeccionId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("SubSeccionId not found"));

		estadoRepository.findById(produccionDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("EstadoId not found"));

		produccionDTO.setId(requestedId);
		produccionDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		produccionRepository.save(produccionMapper.toEntity(produccionDTO));
	}

	/* ---------- DELETE ---------- */

	@Transactional
	public void delete(Long requestId) {
		produccionRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Produccion not found"));

		produccionRepository.deleteById(requestId);
	}

}
