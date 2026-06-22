package com.coagronet.tipounidad.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipounidad.dtos.TipoUnidadDTO;
import com.coagronet.tipounidad.mappers.TipoUnidadMapper;
import com.coagronet.tipounidad.repositories.TipoUnidadRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TipoUnidadService {

	private final TipoUnidadRepository tipoUnidadRepository;

	private final TipoUnidadMapper tipoUnidadMapper;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public List<TipoUnidadDTO> findAll() {
		return tipoUnidadRepository.findAll().stream().map(tipoUnidadMapper::toDTO).toList();
	}

	public Optional<TipoUnidadDTO> findById(Long requestId) {
		return tipoUnidadRepository.findById(requestId)
			.map(tipoUnidadMapper::toDTO);
	}

	@Transactional
	public TipoUnidadDTO create(TipoUnidadDTO tipoUnidadDTO) {
		estadoRepository.findById(tipoUnidadDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		tipoUnidadDTO.setId(null);

		return tipoUnidadMapper.toDTO(tipoUnidadRepository.save(tipoUnidadMapper.toEntity(tipoUnidadDTO)));
	}

	@Transactional
	public void update(Long requestId, TipoUnidadDTO unidadDTO) {
		tipoUnidadRepository.findById(requestId)
			.orElseThrow(() -> new NotFoundException("TipoUnidad no encontrada"));

		estadoRepository.findById(unidadDTO.getEstadoId())
			.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		unidadDTO.setId(requestId);
		tipoUnidadRepository.save(tipoUnidadMapper.toEntity(unidadDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		tipoUnidadRepository.findById(requestId)
			.orElseThrow(() -> new NotFoundException("TipoUnidad no encontrada"));

		tipoUnidadRepository.deleteById(requestId);
	}

}
