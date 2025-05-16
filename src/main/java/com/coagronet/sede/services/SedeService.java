package com.coagronet.sede.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.grupo.repositories.GrupoRepository;
import com.coagronet.municipio.repositories.MunicipioRepository;
import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.mappers.SedeMapper;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.tipoSede.repositories.TipoSedeRepository;
import com.coagronet.user.User;
import com.coagronet.utils.AuthenticationService;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SedeService {

	private final SedeMapper sedeMapper;
	private final SedeRepository sedeRepository;
	private final AuthenticationService authenticationService;
	private final UserEmpresaService userEmpresaService;
	private final GrupoRepository grupoRepository;
	private final TipoSedeRepository tipoSedeRepository;
	private final MunicipioRepository municipioRepository;
	private final EstadoRepository estadoRepository;

	public List<SedeDTO> findAll() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return sedeRepository.findByEmpresaIdOrderByIdAsc(empresa.getId()).stream().map(sedeMapper::toListDto)
				.collect(Collectors.toList());
	}

	public List<SedeDTO> findAllAvailable() {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return sedeRepository.findByEmpresaIdAndEstadoIdNotOrderByIdAsc(empresa.getId(), 2L).stream()
				.map(sedeMapper::toListDto).collect(Collectors.toList());
	}

	public Optional<SedeDTO> findById(Long requestedId) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		return sedeRepository.findByIdAndEmpresaId(requestedId, empresa.getId()).map(sedeMapper::toListDto);
	}

	public SedeDTO create(SedeDTO sedeDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		grupoRepository.findByIdAndEmpresaId(sedeDTO.getGrupoId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El grupo no es válido"));

		tipoSedeRepository.findByIdAndEmpresaId(sedeDTO.getTipoSedeId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El tipo de sede no es válido"));

		municipioRepository.findByIdAndEmpresaId(sedeDTO.getMunicipioId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El municipio no es válido"));

		estadoRepository.findById(sedeDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		sedeDTO.setId(null);
		sedeDTO.setEmpresaId(empresa.getId());

		return sedeMapper.toDTO(sedeRepository.save(sedeMapper.toEntity(sedeDTO)));
	}

	public void update(Long requestedId, SedeDTO sedeDTO) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		sedeRepository.findByIdAndEmpresaId(requestedId, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Sede no encontrado"));

		grupoRepository.findByIdAndEmpresaId(sedeDTO.getGrupoId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El grupo no es válido"));

		tipoSedeRepository.findByIdAndEmpresaId(sedeDTO.getTipoSedeId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El tipo de sede no es válido"));

		municipioRepository.findByIdAndEmpresaId(sedeDTO.getMunicipioId(), empresa.getId())
				.orElseThrow(() -> new BadRequestException("El municipio no es válido"));

		estadoRepository.findById(sedeDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		sedeDTO.setId(requestedId);
		sedeDTO.setEmpresaId(empresa.getId());

		sedeRepository.save(sedeMapper.toEntity(sedeDTO));
	}

	public void delete(Long id) {
		User user = authenticationService.getAuthenticatedUser();
		Empresa empresa = userEmpresaService.getEmpresaFromUser(user);

		sedeRepository.findByIdAndEmpresaId(id, empresa.getId())
				.orElseThrow(() -> new NotFoundException("Sede no encontrado"));

		sedeRepository.deleteById(id);
	}

}
