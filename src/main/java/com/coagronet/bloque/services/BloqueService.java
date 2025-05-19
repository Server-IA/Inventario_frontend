package com.coagronet.bloque.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.bloque.dtos.BloqueDTO;
import com.coagronet.bloque.mappers.BloqueMapper;
import com.coagronet.bloque.repositories.BloqueRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.tipoBloque.repositories.TipoBloqueRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BloqueService {

	private final BloqueMapper bloqueMapper;
	private final BloqueRepository bloqueRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final SedeRepository sedeRepository;
	private final TipoBloqueRepository tipoBloqueRepository;
	private final EstadoRepository estadoRepository;

	public List<BloqueDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return bloqueRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream().map(bloqueMapper::toListDto)
				.collect(Collectors.toList());
	}

	public List<BloqueDTO> findAllAvailable() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return bloqueRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L).stream()
				.map(bloqueMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<BloqueDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return bloqueRepository.findByIdAndEmpresaId(requestedId, empresa.getId()).map(bloqueMapper::toListDto);
	}

	public BloqueDTO create(BloqueDTO bloqueDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		sedeRepository.findByIdAndEmpresaId(bloqueDTO.getSedeId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("La sede no es válida"));

		tipoBloqueRepository.findByIdAndEmpresaId(bloqueDTO.getTipoBloqueId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El tipo de bloque no es válido"));

		estadoRepository.findById(bloqueDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		bloqueDTO.setId(null);
		bloqueDTO.setEmpresaId(empresa.getId());

		return bloqueMapper.toDTO(bloqueRepository.save(bloqueMapper.toEntity(bloqueDTO)));
	}

	public void update(Long requestedId, BloqueDTO bloqueDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		bloqueRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Bloque no encontrado"));

		sedeRepository.findByIdAndEmpresaId(bloqueDTO.getSedeId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("La sede no es válida"));

		tipoBloqueRepository.findByIdAndEmpresaId(bloqueDTO.getTipoBloqueId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El tipo de bloque no es válido"));

		estadoRepository.findById(bloqueDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		bloqueDTO.setId(requestedId);
		bloqueDTO.setEmpresaId(empresa.getId());

		bloqueRepository.save(bloqueMapper.toEntity(bloqueDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		bloqueRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Bloque no encontrado"));

		bloqueRepository.deleteById(id);
	}

}
