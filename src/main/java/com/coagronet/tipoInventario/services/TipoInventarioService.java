package com.coagronet.tipoInventario.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.tipoInventario.dtos.TipoInventarioDTO;
import com.coagronet.tipoInventario.mappers.TipoInventarioMapper;
import com.coagronet.tipoInventario.repositories.TipoInventarioRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoInventarioService {

	private final TipoInventarioRepository tipoInventarioRepository;

	private final EstadoRepository estadoRepository;

	private final TipoInventarioMapper tipoInventarioMapper;

	private final UserEmpresaService userEmpresaService;

	public List<TipoInventarioDTO> findAll() {
		return tipoInventarioRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream()
				.map(tipoInventarioMapper::toDTO)
				.collect(Collectors.toList());
	}

	public Optional<TipoInventarioDTO> findById(Long requestedId) {
		return tipoInventarioRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(tipoInventarioMapper::toDTO);
	}

	@Transactional
	public TipoInventarioDTO create(TipoInventarioDTO tipoInventarioDTO) {
		estadoRepository.findById(tipoInventarioDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("Estado no encontrado o no válido"));

		tipoInventarioDTO.setId(null);
		tipoInventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return tipoInventarioMapper
				.toDTO(tipoInventarioRepository.save(tipoInventarioMapper.toEntity(tipoInventarioDTO)));
	}

	@Transactional
	public void update(Long requestedId, TipoInventarioDTO tipoInventarioDTO) {
		tipoInventarioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Tipo inventario no encontrado en su empresa"));

		estadoRepository.findById(tipoInventarioDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		tipoInventarioDTO.setId(requestedId);
		tipoInventarioDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		tipoInventarioRepository.save(tipoInventarioMapper.toEntity(tipoInventarioDTO));
	}

	@Transactional
	public void delete(Long requestedId) {
		tipoInventarioRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Tipo inventario no encontrado en su empresa"));

		tipoInventarioRepository.deleteById(requestedId);
	}

}
