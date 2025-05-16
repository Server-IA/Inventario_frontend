package com.coagronet.pais.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.pais.dtos.PaisDTO;
import com.coagronet.pais.mappers.PaisMapper;
import com.coagronet.pais.repositories.PaisRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaisService {

	private final PaisMapper paisMapper;
	private final PaisRepository paisRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final EstadoRepository estadoRepository;

	public List<PaisDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return paisRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream().map(paisMapper::toListDto)
				.collect(Collectors.toList());
	}

	public List<PaisDTO> findAllAvailable() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return paisRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L).stream()
				.map(paisMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<PaisDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return paisRepository.findByIdAndEmpresaId(requestedId, empresa.getId()).map(paisMapper::toListDto);
	}

	public PaisDTO create(PaisDTO paisDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		estadoRepository.findById(paisDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		paisDTO.setId(null);
		paisDTO.setEmpresaId(empresa.getId());

		return paisMapper.toDTO(paisRepository.save(paisMapper.toEntity(paisDTO)));
	}

	public void update(Long requestedId, PaisDTO paisDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		paisRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Pais no encontrado"));

		estadoRepository.findById(paisDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		paisDTO.setId(requestedId);
		paisDTO.setEmpresaId(empresa.getId());

		paisRepository.save(paisMapper.toEntity(paisDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		paisRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Pais no encontrado"));

		paisRepository.deleteById(id);
	}

}