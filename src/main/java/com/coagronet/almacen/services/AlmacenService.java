package com.coagronet.almacen.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.almacen.dtos.AlmacenDTO;
import com.coagronet.almacen.mappers.AlmacenMapper;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlmacenService {

	private final AlmacenMapper almacenMapper;

	private final AlmacenRepository almacenRepository;

	private final UserEmpresaService userEmpresaService;

	private final EspacioRepository espacioRepository;

	private final EstadoRepository estadoRepository;

	public Page<AlmacenDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		return almacenRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(almacenMapper::toListDto);
	}

	public Optional<AlmacenDTO> findById(Long requestedId) {

		return almacenRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(almacenMapper::toListDto);
	}

	public AlmacenDTO create(AlmacenDTO almacenDTO) {
		espacioRepository
				.findByIdAndEmpresaId(almacenDTO.getEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El espacio no es v�lido"));

		estadoRepository.findById(almacenDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		almacenDTO.setId(null);
		almacenDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return almacenMapper.toDTO(almacenRepository.save(almacenMapper.toEntity(almacenDTO)));
	}

	public void update(Long requestedId, AlmacenDTO almacenDTO) {
		almacenRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Almacen no encontrado"));

		espacioRepository
				.findByIdAndEmpresaId(almacenDTO.getEspacioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El espacio no es v�lida"));

		estadoRepository.findById(almacenDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		almacenDTO.setId(requestedId);
		almacenDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		almacenRepository.save(almacenMapper.toEntity(almacenDTO));
	}

	public void delete(Long id) {
		almacenRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Almacen no encontrado"));

		almacenRepository.deleteById(id);
	}

}
