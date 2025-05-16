package com.coagronet.grupo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.grupo.mappers.GrupoMapper;
import com.coagronet.grupo.repositories.GrupoRepository;
import com.coagronet.grupo.dtos.GrupoDTO;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrupoService {

	private final GrupoMapper grupoMapper;
	private final GrupoRepository grupoRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final EstadoRepository estadoRepository;
	
	public List<GrupoDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return grupoRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream().map(grupoMapper::toListDto)
				.collect(Collectors.toList());
	}

	public List<GrupoDTO> findAllAvailable() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return grupoRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L).stream()
				.map(grupoMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<GrupoDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return grupoRepository.findByIdAndEmpresaId(requestedId, empresa.getId()).map(grupoMapper::toListDto);
	}

	public GrupoDTO create(GrupoDTO grupoDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		estadoRepository.findById(grupoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		grupoDTO.setId(null);
		grupoDTO.setEmpresaId(empresa.getId());

		return grupoMapper.toDTO(grupoRepository.save(grupoMapper.toEntity(grupoDTO)));
	}

	public void update(Long requestedId, GrupoDTO grupoDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		grupoRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

		estadoRepository.findById(grupoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		grupoDTO.setId(requestedId);
		grupoDTO.setEmpresaId(empresa.getId());

		grupoRepository.save(grupoMapper.toEntity(grupoDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		grupoRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Grupo no encontrado"));

		grupoRepository.deleteById(id);
	}

}
