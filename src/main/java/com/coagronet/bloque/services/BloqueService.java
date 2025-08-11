package com.coagronet.bloque.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.bloque.dtos.BloqueDTO;
import com.coagronet.bloque.mappers.BloqueMapper;
import com.coagronet.bloque.repositories.BloqueRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.tipoBloque.repositories.TipoBloqueRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BloqueService {

	private final BloqueMapper bloqueMapper;

	private final BloqueRepository bloqueRepository;

	private final UserEmpresaService userEmpresaService;

	private final SedeRepository sedeRepository;

	private final TipoBloqueRepository tipoBloqueRepository;

	private final EstadoRepository estadoRepository;

	public Page<BloqueDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return bloqueRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(bloqueMapper::toListDto);
	}

	public Optional<BloqueDTO> findById(Long requestedId) {
		return bloqueRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(bloqueMapper::toListDto);
	}

	public BloqueDTO create(BloqueDTO bloqueDTO) {
		sedeRepository.findByIdAndEmpresaId(bloqueDTO.getSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("La sede no es válida"));

		tipoBloqueRepository
			.findByIdAndEmpresaId(bloqueDTO.getTipoBloqueId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El tipo de bloque no es válido"));

		estadoRepository.findById(bloqueDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		bloqueDTO.setId(null);
		bloqueDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return bloqueMapper.toDTO(bloqueRepository.save(bloqueMapper.toEntity(bloqueDTO)));
	}

	public void update(Long requestedId, BloqueDTO bloqueDTO) {
		bloqueRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Bloque no encontrado"));

		sedeRepository.findByIdAndEmpresaId(bloqueDTO.getSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("La sede no es válida"));

		tipoBloqueRepository
			.findByIdAndEmpresaId(bloqueDTO.getTipoBloqueId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El tipo de bloque no es válido"));

		estadoRepository.findById(bloqueDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		bloqueDTO.setId(requestedId);
		bloqueDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		bloqueRepository.save(bloqueMapper.toEntity(bloqueDTO));
	}

	public void delete(Long id) {
		bloqueRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Bloque no encontrado"));

		bloqueRepository.deleteById(id);
	}

}
