package com.coagronet.movimiento.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.movimiento.dtos.MovimientoDTO;
import com.coagronet.movimiento.mappers.MovimientoMapper;
import com.coagronet.movimiento.repositories.MovimientoRepository;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService {

	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final MovimientoMapper movimientoMapper;
	private final MovimientoRepository movimientoRepository;
	private final EstadoRepository estadoRepository;

	public List<MovimientoDTO> findAll() {
		return movimientoRepository
				.findByEmpresaIdOrderByIdAsc(
						(userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser())).getId())
				.stream().map(movimientoMapper::toListDTO).collect(Collectors.toList());
	}

	public Optional<MovimientoDTO> findById(Long requestedId) {
		return movimientoRepository
				.findByIdAndEmpresaId(requestedId,
						(userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser())).getId())
				.map(movimientoMapper::toListDTO);
	}

	public MovimientoDTO create(MovimientoDTO movimientoDTO) {
		estadoRepository.findById(movimientoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		movimientoDTO.setId(null);
		movimientoDTO.setEmpresaId(
				(userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser())).getId());

		return movimientoMapper.toDTO(movimientoRepository.save(movimientoMapper.toEntity(movimientoDTO)));
	}

	public void update(Long requestedId, MovimientoDTO movimientoDTO) {
		movimientoRepository
				.findByIdAndEmpresaId(requestedId,
						(userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser())).getId())
				.orElseThrow(() -> new NotFoundException("El movimiento no fue encontrado."));

		estadoRepository.findById(movimientoDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		movimientoDTO.setId(requestedId);
		movimientoDTO.setEmpresaId(
				(userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser())).getId());

		movimientoRepository.save(movimientoMapper.toEntity(movimientoDTO));
	}

	public void delete(Long id) {
		movimientoRepository
				.findByIdAndEmpresaId(id,
						(userEmpresaService.getEmpresaFromUser(authenticationService.getAuthenticatedUser())).getId())
				.orElseThrow(() -> new NotFoundException("El movimiento no fue encontrado."));

		movimientoRepository.deleteById(id);
	}

}
