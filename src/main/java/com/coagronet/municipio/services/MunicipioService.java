package com.coagronet.municipio.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.departamento.repositories.DepartamentoRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.municipio.dtos.MunicipioDTO;
import com.coagronet.municipio.mappers.MunicipioMapper;
import com.coagronet.municipio.repositories.MunicipioRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MunicipioService {

	private final MunicipioMapper municipioMapper;
	private final MunicipioRepository municipioRepository;
	private final DepartamentoRepository departamentoRepository;
	private final EstadoRepository estadoRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;

	public List<MunicipioDTO> findAll(Long departamentoId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return municipioRepository.findByDepartamentoIdAndEmpresaIdOrderByIdAsc(departamentoId, empresa.getId())
				.stream().map(municipioMapper::toListDto).collect(Collectors.toList());
	}

	public List<MunicipioDTO> findAllAvailable(Long departamentoId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return municipioRepository
				.findByDepartamentoIdAndEmpresaIdAndEstadoIdNotOrderByIdAsc(departamentoId, empresa.getId(), 2L)
				.stream().map(municipioMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<MunicipioDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return municipioRepository.findByIdAndEmpresaId(requestedId, empresa.getId()).map(municipioMapper::toListDto);
	}

	public MunicipioDTO create(MunicipioDTO municipioDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		departamentoRepository.findByIdAndEmpresaId(municipioDTO.getDepartamentoId(), empresa.getId())
				.orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

		estadoRepository.findById(municipioDTO.getEstadoId())
				.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		municipioDTO.setId(null);
		municipioDTO.setEmpresaId(empresa.getId());

		return municipioMapper.toDTO(municipioRepository.save(municipioMapper.toEntity(municipioDTO)));
	}

	public void update(Long requestedId, MunicipioDTO municipioDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		municipioRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Municipio no encontrado"));

		departamentoRepository.findByIdAndEmpresaId(municipioDTO.getDepartamentoId(), empresa.getId())
				.orElseThrow(() -> new NotFoundException("Departamento no encontrado"));

		estadoRepository.findById(municipioDTO.getEstadoId())
				.orElseThrow(() -> new NotFoundException("Estado no encontrado"));

		municipioDTO.setId(requestedId);
		municipioDTO.setEmpresaId(empresa.getId());

		municipioRepository.save(municipioMapper.toEntity(municipioDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		municipioRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Municipio no encontrado"));

		municipioRepository.deleteById(id);
	}

}