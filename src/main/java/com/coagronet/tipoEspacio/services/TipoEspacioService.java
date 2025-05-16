package com.coagronet.tipoEspacio.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.tipoEspacio.dtos.TipoEspacioDTO;
import com.coagronet.tipoEspacio.mappers.TipoEspacioMapper;
import com.coagronet.tipoEspacio.repositories.TipoEspacioRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoEspacioService {

	private final TipoEspacioRepository tipoEspacioRepository;
	private final TipoEspacioMapper tipoEspacioMapper;
	private final EstadoRepository estadoRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;

	public List<TipoEspacioDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return tipoEspacioRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream()
				.map(tipoEspacioMapper::toListDto).collect(Collectors.toList());
	}

	public List<TipoEspacioDTO> findAllAvailable() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return tipoEspacioRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L).stream()
				.map(tipoEspacioMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<TipoEspacioDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return tipoEspacioRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.map(tipoEspacioMapper::toListDto);
	}

	public TipoEspacioDTO create(TipoEspacioDTO tipoEspacioDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		estadoRepository.findById(tipoEspacioDTO.getEstadoId())
				.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		tipoEspacioDTO.setId(null);
		tipoEspacioDTO.setEmpresaId(empresa.getId());

		return tipoEspacioMapper.toDTO(tipoEspacioRepository.save(tipoEspacioMapper.toEntity(tipoEspacioDTO)));
	}

	public void update(Long requestedId, TipoEspacioDTO tipoEspacioDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		tipoEspacioRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Tipo de espacio no encontrado"));

		estadoRepository.findById(tipoEspacioDTO.getEstadoId())
				.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		tipoEspacioDTO.setId(requestedId);
		tipoEspacioDTO.setEmpresaId(empresa.getId());

		tipoEspacioRepository.save(tipoEspacioMapper.toEntity(tipoEspacioDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		tipoEspacioRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Tipo de espacio no encontrado"));

		tipoEspacioRepository.deleteById(id);
	}

}