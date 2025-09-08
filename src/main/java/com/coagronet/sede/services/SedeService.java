package com.coagronet.sede.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.grupo.repositories.GrupoRepository;
import com.coagronet.municipio.repositories.MunicipioRepository;
import com.coagronet.sede.dtos.SedeDTO;
import com.coagronet.sede.mappers.SedeMapper;
import com.coagronet.sede.repositories.SedeRepository;
import com.coagronet.tipoSede.repositories.TipoSedeRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SedeService {

	private final SedeMapper sedeMapper;

	private final SedeRepository sedeRepository;

	private final UserEmpresaService userEmpresaService;

	private final GrupoRepository grupoRepository;

	private final TipoSedeRepository tipoSedeRepository;

	private final MunicipioRepository municipioRepository;

	private final EstadoRepository estadoRepository;

	public Page<SedeDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return sedeRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(sedeMapper::toListDto);
	}

	public Optional<SedeDTO> findById(Long requestedId) {
		return sedeRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(sedeMapper::toListDto);
	}

	public SedeDTO create(SedeDTO sedeDTO) {
		grupoRepository.findByIdAndEmpresaId(sedeDTO.getGrupoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El grupo no es válido"));

		tipoSedeRepository
			.findByIdAndEmpresaId(sedeDTO.getTipoSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El tipo de sede no es válido"));

		municipioRepository
			.findByIdAndEmpresaId(sedeDTO.getMunicipioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El municipio no es válido"));

		estadoRepository.findById(sedeDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		sedeDTO.setId(null);
		sedeDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return sedeMapper.toDTO(sedeRepository.save(sedeMapper.toEntity(sedeDTO)));
	}

	public void update(Long requestedId, SedeDTO sedeDTO) {
		sedeRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Sede no encontrado"));

		grupoRepository.findByIdAndEmpresaId(sedeDTO.getGrupoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El grupo no es válido"));

		tipoSedeRepository
			.findByIdAndEmpresaId(sedeDTO.getTipoSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El tipo de sede no es válido"));

		municipioRepository
			.findByIdAndEmpresaId(sedeDTO.getMunicipioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El municipio no es válido"));

		estadoRepository.findById(sedeDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido"));

		sedeDTO.setId(requestedId);
		sedeDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		sedeRepository.save(sedeMapper.toEntity(sedeDTO));
	}

	public void delete(Long id) {
		sedeRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Sede no encontrado"));

		sedeRepository.deleteById(id);
	}

}
