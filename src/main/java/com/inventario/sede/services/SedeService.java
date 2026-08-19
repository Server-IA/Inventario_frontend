package com.inventario.sede.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.grupo.repositories.GrupoRepository;
import com.inventario.municipio.repositories.MunicipioRepository;
import com.inventario.sede.dtos.SedeDTO;
import com.inventario.sede.mappers.SedeMapper;
import com.inventario.sede.repositories.SedeRepository;
import com.inventario.tipoSede.repositories.TipoSedeRepository;
import com.inventario.utils.UserEmpresaService;

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
		return sedeRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(sedeMapper::toListDto);
	}

	public Optional<SedeDTO> findById(Long requestedId) {
		return sedeRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(sedeMapper::toListDto);
	}

	public SedeDTO create(SedeDTO sedeDTO) {
		grupoRepository.findByIdAndEmpresaId(sedeDTO.getGrupoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El grupo no es v�lido"));

		tipoSedeRepository
				.findByIdAndEmpresaId(sedeDTO.getTipoSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El tipo de sede no es v�lido"));

		municipioRepository
				.findByIdAndEmpresaId(sedeDTO.getMunicipioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El municipio no es v�lido"));

		estadoRepository.findById(sedeDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

		sedeDTO.setId(null);
		sedeDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		return sedeMapper.toDTO(sedeRepository.save(sedeMapper.toEntity(sedeDTO)));
	}

	public void update(Long requestedId, SedeDTO sedeDTO) {
		sedeRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new NotFoundException("Sede no encontrado"));

		grupoRepository.findByIdAndEmpresaId(sedeDTO.getGrupoId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El grupo no es v�lido"));

		tipoSedeRepository
				.findByIdAndEmpresaId(sedeDTO.getTipoSedeId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El tipo de sede no es v�lido"));

		municipioRepository
				.findByIdAndEmpresaId(sedeDTO.getMunicipioId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
				.orElseThrow(() -> new BadRequestException("El municipio no es v�lido"));

		estadoRepository.findById(sedeDTO.getEstadoId())
				.orElseThrow(() -> new BadRequestException("El estado no es v�lido"));

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
