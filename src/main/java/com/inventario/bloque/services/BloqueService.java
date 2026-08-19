package com.inventario.bloque.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.inventario.bloque.dtos.BloqueDTO;
import com.inventario.bloque.mappers.BloqueMapper;
import com.inventario.bloque.repositories.BloqueRepository;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.sede.repositories.SedeRepository;
import com.inventario.tipoBloque.repositories.TipoBloqueRepository;
import com.inventario.utils.UserEmpresaService;

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
		return bloqueRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(bloqueMapper::toListDto);
	}

	public Optional<BloqueDTO> findById(Long requestedId) {
		return bloqueRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(bloqueMapper::toListDto);
	}

	public BloqueDTO create(BloqueDTO bloqueDTO) {
		sedeRepository.findByIdAndEmpresaId(bloqueDTO.getSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("La sede no es v�lida"));

		tipoBloqueRepository
				.findByIdAndEmpresaId(bloqueDTO.getTipoBloqueId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El tipo de bloque no es v�lido"));

		estadoRepository.findById(bloqueDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		bloqueDTO.setId(null);
		bloqueDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return bloqueMapper.toDTO(bloqueRepository.save(bloqueMapper.toEntity(bloqueDTO)));
	}

	public void update(Long requestedId, BloqueDTO bloqueDTO) {
		bloqueRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Bloque no encontrado"));

		sedeRepository.findByIdAndEmpresaId(bloqueDTO.getSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("La sede no es v�lida"));

		tipoBloqueRepository
				.findByIdAndEmpresaId(bloqueDTO.getTipoBloqueId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El tipo de bloque no es v�lido"));

		estadoRepository.findById(bloqueDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

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
