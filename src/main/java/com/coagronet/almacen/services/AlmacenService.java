package com.coagronet.almacen.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.almacen.dtos.AlmacenDTO;
import com.coagronet.almacen.mappers.AlmacenMapper;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlmacenService {

	private final AlmacenMapper almacenMapper;
	private final AlmacenRepository almacenRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final EspacioRepository espacioRepository;
	private final EstadoRepository estadoRepository;

	public List<AlmacenDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return almacenRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream().map(almacenMapper::toListDto)
				.collect(Collectors.toList());
	}

	public List<AlmacenDTO> findAllAvailable() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return almacenRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L).stream()
				.map(almacenMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<AlmacenDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return almacenRepository.findByIdAndEmpresaId(requestedId, empresa.getId()).map(almacenMapper::toListDto);
	}

	public AlmacenDTO create(AlmacenDTO almacenDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		espacioRepository.findByIdAndEmpresaId(almacenDTO.getEspacioId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El espacio no es válido"));

		estadoRepository.findById(almacenDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		almacenDTO.setId(null);
		almacenDTO.setEmpresaId(empresa.getId());

		return almacenMapper.toDTO(almacenRepository.save(almacenMapper.toEntity(almacenDTO)));
	}

	public void update(Long requestedId, AlmacenDTO almacenDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		almacenRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Almacen no encontrado"));

		espacioRepository.findByIdAndEmpresaId(almacenDTO.getEspacioId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El espacio no es válida"));

		estadoRepository.findById(almacenDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		almacenDTO.setId(requestedId);
		almacenDTO.setEmpresaId(empresa.getId());

		almacenRepository.save(almacenMapper.toEntity(almacenDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		almacenRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Almacen no encontrado"));

		almacenRepository.deleteById(id);
	}

}
