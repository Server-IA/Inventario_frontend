package com.inventario.tipoSede.services;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.tipoSede.dtos.TipoSedeDTO;
import com.inventario.tipoSede.mappers.TipoSedeMapper;
import com.inventario.tipoSede.repositories.TipoSedeRepository;
import com.inventario.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TipoSedeService {

	private final TipoSedeRepository tipoSedeRepository;

	private final TipoSedeMapper tipoSedeMapper;

	private final UserEmpresaService userEmpresaService;

	private final EstadoRepository estadoRepository;

	public List<TipoSedeDTO> findAll() {
		return tipoSedeRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream()
				.map(tipoSedeMapper::toListDTO)
				.collect(Collectors.toList());
	}

	public Optional<TipoSedeDTO> findById(Long id) {
		return tipoSedeRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(tipoSedeMapper::toListDTO);
	}

	@Transactional
	public TipoSedeDTO create(TipoSedeDTO tipoSedeDTO) {
		estadoRepository.findById(tipoSedeDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		tipoSedeDTO.setId(null);
		tipoSedeDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return tipoSedeMapper.toDTO(tipoSedeRepository.save(tipoSedeMapper.toEntity(tipoSedeDTO)));
	}

	@Transactional
	public void update(Long requestedId, TipoSedeDTO tipoSedeDTO) {
		tipoSedeRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Tipo de sede no encontrada"));

		tipoSedeDTO.setId(requestedId);
		tipoSedeDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		tipoSedeRepository.save(tipoSedeMapper.toEntity(tipoSedeDTO));
	}

	public void delete(Long id) {
		tipoSedeRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Tipo de sede no encontrada"));

		tipoSedeRepository.deleteById(id);
	}

}
