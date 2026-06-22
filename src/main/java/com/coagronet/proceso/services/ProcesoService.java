package com.coagronet.proceso.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.proceso.dtos.ProcesoDTO;
import com.coagronet.proceso.mappers.ProcesoMapper;
import com.coagronet.proceso.repositories.ProcesoRepository;
import com.coagronet.tipoProduccion.repositories.TipoProduccionRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcesoService {

	private final UserEmpresaService userEmpresaService;

	private final ProcesoMapper procesoMapper;

	private final ProcesoRepository procesoRepository;

	private final TipoProduccionRepository tipoProduccionRepository;

	private final EstadoRepository estadoRepository;

	public List<ProcesoDTO> findAll() {
		return procesoRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(procesoMapper::toListDTO)
			.collect(Collectors.toList());
	}

	public Optional<ProcesoDTO> findById(Long requestedId) {
		return procesoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(procesoMapper::toListDTO);
	}

	public ProcesoDTO create(ProcesoDTO procesoDTO) {
		tipoProduccionRepository.findById(procesoDTO.getTipoProduccionId())
			.orElseThrow(() -> new BadRequestException("El tipo de producción no es válido"));

		estadoRepository.findById(procesoDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		procesoDTO.setId(null);
		procesoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return procesoMapper.toDTO(procesoRepository.save(procesoMapper.toEntity(procesoDTO)));
	}

	public void update(Long requestedId, ProcesoDTO procesoDTO) {
		procesoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El proceso no fue encontrado."));

		tipoProduccionRepository.findById(procesoDTO.getTipoProduccionId())
			.orElseThrow(() -> new BadRequestException("El tipo de producción no es válido"));

		estadoRepository.findById(procesoDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		procesoDTO.setId(requestedId);
		procesoDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		procesoRepository.save(procesoMapper.toEntity(procesoDTO));
	}

	public void delete(Long id) {
		procesoRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El proceso no fue encontrado."));

		procesoRepository.deleteById(id);
	}

}
