package com.coagronet.proveedor.services;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.proveedor.dtos.ProveedorDTO;
import com.coagronet.proveedor.mappers.ProveedorMapper;
import com.coagronet.proveedor.repositories.ProveedorRepository;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {

	private final ProveedorRepository proveedorRepository;

	private final EstadoRepository estadoRepository;

	private final ProveedorMapper proveedorMapper;

	private final UserEmpresaService userEmpresaService;

	public List<ProveedorDTO> findAll() {
		return proveedorRepository.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream()
				.map(proveedorMapper::toDto)
				.collect(Collectors.toList());
	}

	public Optional<ProveedorDTO> findById(Long requestedId) {
		return proveedorRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(proveedorMapper::toDto);
	}

	@Transactional
	public ProveedorDTO create(ProveedorDTO proveedorDTO) {
		estadoRepository.findById(proveedorDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		proveedorDTO.setId(null);
		proveedorDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return proveedorMapper.toDto(proveedorRepository.save(proveedorMapper.toEntity(proveedorDTO)));
	}

	@Transactional
	public void update(Long requestedId, ProveedorDTO proveedorDTO) {
		proveedorRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Proveedor no encontrada o no válida"));

		estadoRepository.findById(proveedorDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		proveedorDTO.setId(requestedId);
		proveedorDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		proveedorRepository.save(proveedorMapper.toEntity(proveedorDTO));
	}

	@Transactional
	public void delete(Long requestId) {
		proveedorRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Proveedor no encontrado o no válido"));

		proveedorRepository.deleteById(requestId);
	}

}
