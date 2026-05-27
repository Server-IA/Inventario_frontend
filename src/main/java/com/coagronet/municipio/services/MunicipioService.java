package com.coagronet.municipio.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.coagronet.departamento.Departamento;
import com.coagronet.departamento.repositories.DepartamentoRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.municipio.dtos.MunicipioDTO;
import com.coagronet.municipio.mappers.MunicipioMapper;
import com.coagronet.municipio.repositories.MunicipioRepository;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MunicipioService {

	private final MunicipioMapper municipioMapper;

	private final MunicipioRepository municipioRepository;

	private final DepartamentoRepository departamentoRepository;

	private final EstadoRepository estadoRepository;

	public List<MunicipioDTO> findAll(Long departamentoId, String nombre, Integer codigo, String acronimo, Long estadoId) {
		return municipioRepository
			.findByDepartamentoIdOrderByIdAsc(departamentoId)
			.stream()
			.filter(municipio -> matchesText(municipio.getNombre(), nombre))
			.filter(municipio -> codigo == null || codigo.equals(municipio.getCodigo()))
			.filter(municipio -> matchesText(municipio.getAcronimo(), acronimo))
			.filter(municipio -> estadoId == null
					|| (municipio.getEstado() != null && estadoId.equals(municipio.getEstado().getId())))
			.map(municipioMapper::toListDto)
			.collect(Collectors.toList());
	}

	public Optional<MunicipioDTO> findById(Long requestedId) {
		return municipioRepository.findById(requestedId).map(municipioMapper::toListDto);
	}

	public MunicipioDTO create(MunicipioDTO municipioDTO) {
		validateActiveDepartment(municipioDTO.getDepartamentoId());
		validateGeneralStatus(municipioDTO.getEstadoId(), "municipality.status.not.valid");
		validateUniqueFields(municipioDTO, null);

		municipioDTO.setId(null);

		return municipioMapper.toDTO(municipioRepository.save(municipioMapper.toEntity(municipioDTO)));
	}

	public void update(Long requestedId, MunicipioDTO municipioDTO) {
		municipioRepository.findById(requestedId)
			.orElseThrow(() -> new NotFoundException("municipality.not-found.with-id", requestedId));

		Departamento departamento = departamentoRepository.findById(municipioDTO.getDepartamentoId())
			.orElseThrow(() -> new BadRequestException("municipality.department.not.valid"));
		validateGeneralStatus(municipioDTO.getEstadoId(), "municipality.status.not.valid");
		validateCanBeActive(municipioDTO.getEstadoId(), departamento);
		validateUniqueFields(municipioDTO, requestedId);

		municipioDTO.setId(requestedId);

		municipioRepository.save(municipioMapper.toEntity(municipioDTO));
	}

	public void delete(Long id) {
		var municipio = municipioRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("municipality.not-found.with-id", id));
		Estado inactiveStatus = getInactiveStatus();

		municipio.setEstado(inactiveStatus);
		municipioRepository.save(municipio);
	}

	private Departamento validateActiveDepartment(Long departamentoId) {
		Departamento departamento = departamentoRepository.findById(departamentoId)
			.orElseThrow(() -> new BadRequestException("municipality.department.not.valid"));

		if (departamento.getEstado() == null
				|| !EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(departamento.getEstado().getId())) {
			throw new BadRequestException("municipality.department.must-be-active");
		}

		if (departamento.getPais() == null || departamento.getPais().getEstado() == null
				|| !EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(departamento.getPais().getEstado().getId())) {
			throw new BadRequestException("municipality.country.must-be-active");
		}

		return departamento;
	}

	private Estado validateGeneralStatus(Long estadoId, String errorCode) {
		Estado estado = estadoRepository.findById(estadoId)
			.orElseThrow(() -> new BadRequestException(errorCode));

		if (!EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(estado.getId())
				&& !EstadoConstantes.ESTADO_GENERAL_INACTIVO.equals(estado.getId())) {
			throw new BadRequestException(errorCode);
		}

		return estado;
	}

	private Estado getInactiveStatus() {
		return estadoRepository.findById(EstadoConstantes.ESTADO_GENERAL_INACTIVO)
			.orElseThrow(() -> new BadRequestException("municipality.status.not.valid"));
	}

	private void validateCanBeActive(Long estadoId, Departamento departamento) {
		if (!EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(estadoId)) {
			return;
		}

		if (departamento.getEstado() == null
				|| !EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(departamento.getEstado().getId())) {
			throw new BadRequestException("municipality.department.must-be-active");
		}

		if (departamento.getPais() == null || departamento.getPais().getEstado() == null
				|| !EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(departamento.getPais().getEstado().getId())) {
			throw new BadRequestException("municipality.country.must-be-active");
		}
	}

	private void validateUniqueFields(MunicipioDTO municipioDTO, Long currentId) {
		Long departamentoId = municipioDTO.getDepartamentoId();
		if (currentId == null) {
			if (municipioRepository.existsByDepartamentoIdAndNombreIgnoreCase(departamentoId, municipioDTO.getNombre())) {
				throw new BadRequestException("municipality.name.duplicate");
			}
			if (municipioDTO.getCodigo() != null
					&& municipioRepository.existsByDepartamentoIdAndCodigo(departamentoId, municipioDTO.getCodigo())) {
				throw new BadRequestException("municipality.code.duplicate");
			}
			if (municipioDTO.getAcronimo() != null
					&& municipioRepository.existsByDepartamentoIdAndAcronimoIgnoreCase(departamentoId,
							municipioDTO.getAcronimo())) {
				throw new BadRequestException("municipality.acronym.duplicate");
			}
			return;
		}

		if (municipioRepository.existsByDepartamentoIdAndNombreIgnoreCaseAndIdNot(departamentoId,
				municipioDTO.getNombre(), currentId)) {
			throw new BadRequestException("municipality.name.duplicate");
		}
		if (municipioDTO.getCodigo() != null && municipioRepository.existsByDepartamentoIdAndCodigoAndIdNot(
				departamentoId, municipioDTO.getCodigo(), currentId)) {
			throw new BadRequestException("municipality.code.duplicate");
		}
		if (municipioDTO.getAcronimo() != null && municipioRepository.existsByDepartamentoIdAndAcronimoIgnoreCaseAndIdNot(
				departamentoId, municipioDTO.getAcronimo(), currentId)) {
			throw new BadRequestException("municipality.acronym.duplicate");
		}
	}

	private boolean matchesText(String value, String filter) {
		return filter == null || filter.isBlank()
				|| (value != null && value.toLowerCase().contains(filter.trim().toLowerCase()));
	}

}
