package com.inventario.ingrediente.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.ingrediente.dtos.IngredienteDTO;
import com.inventario.ingrediente.mappers.IngredienteMapper;
import com.inventario.ingrediente.repositories.IngredienteRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IngredienteService {

	private final IngredienteRepository ingredienteRepository;

	private final IngredienteMapper ingredienteMapper;

	private final EstadoRepository estadoRepository;

	private final UserEmpresaService userEmpresaService;

	public Page<IngredienteDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return ingredienteRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
				.map(ingredienteMapper::toListDto);
	}

	public Optional<IngredienteDTO> findById(Long requestedId) {
		return ingredienteRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(ingredienteMapper::toListDto);
	}

	public IngredienteDTO create(IngredienteDTO ingredienteDTO) {
		estadoRepository.findById(ingredienteDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		ingredienteDTO.setId(null);
		ingredienteDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return ingredienteMapper.toDTO(ingredienteRepository.save(ingredienteMapper.toEntity(ingredienteDTO)));
	}

	public void update(Long requestedId, IngredienteDTO ingredienteDTO) {
		ingredienteRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Ingrediente no encontrado."));

		estadoRepository.findById(ingredienteDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		ingredienteDTO.setId(requestedId);
		ingredienteDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		ingredienteRepository.save(ingredienteMapper.toEntity(ingredienteDTO));
	}

	public void delete(Long id) {
		ingredienteRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Ingrediente no encontrado."));

		ingredienteRepository.deleteById(id);
	}

}
