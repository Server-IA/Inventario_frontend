package com.coagronet.espacio.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.espacio.dtos.EspacioDTO;
import com.coagronet.espacio.mappers.EspacioMapper;
import com.coagronet.espacio.repositories.EspacioRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.bloque.repositories.BloqueRepository;
import com.coagronet.tipoEspacio.repositories.TipoEspacioRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EspacioService {

	private final EspacioMapper espacioMapper;
	private final EspacioRepository espacioRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final BloqueRepository bloqueRepository;
	private final TipoEspacioRepository tipoEspacioRepository;
	private final EstadoRepository estadoRepository;

	public List<EspacioDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return espacioRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream().map(espacioMapper::toListDto)
				.collect(Collectors.toList());
	}

	public List<EspacioDTO> findAllAvailable() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return espacioRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L).stream()
				.map(espacioMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<EspacioDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return espacioRepository.findByIdAndEmpresaId(requestedId, empresa.getId()).map(espacioMapper::toListDto);
	}

	public EspacioDTO create(EspacioDTO espacioDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		bloqueRepository.findByIdAndEmpresaId(espacioDTO.getBloqueId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El bloque no es válido"));

		tipoEspacioRepository.findByIdAndEmpresaId(espacioDTO.getTipoEspacioId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El tipo de espacio no es válido"));

		estadoRepository.findById(espacioDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		espacioDTO.setId(null);
		espacioDTO.setEmpresaId(empresa.getId());

		return espacioMapper.toDTO(espacioRepository.save(espacioMapper.toEntity(espacioDTO)));
	}

	public void update(Long requestedId, EspacioDTO espacioDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		espacioRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Espacio no encontrado"));

		bloqueRepository.findByIdAndEmpresaId(espacioDTO.getBloqueId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El bloque no es válida"));

		tipoEspacioRepository.findByIdAndEmpresaId(espacioDTO.getTipoEspacioId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El tipo de espacio no es válido"));

		estadoRepository.findById(espacioDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		espacioDTO.setId(requestedId);
		espacioDTO.setEmpresaId(empresa.getId());

		espacioRepository.save(espacioMapper.toEntity(espacioDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		espacioRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Espacio no encontrado"));

		espacioRepository.deleteById(id);
	}

}
