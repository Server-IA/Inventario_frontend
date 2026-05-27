package com.coagronet.departamento.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import org.springframework.stereotype.Service;

import com.coagronet.departamento.Departamento;
import com.coagronet.departamento.dtos.DepartamentoDTO;
import com.coagronet.departamento.mappers.DepartamentoMapper;
import com.coagronet.departamento.repositories.DepartamentoRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.municipio.Municipio;
import com.coagronet.municipio.repositories.MunicipioRepository;
import com.coagronet.pais.Pais;
import com.coagronet.pais.repositories.PaisRepository;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepartamentoService {

	private final DepartamentoMapper departamentoMapper;

	private final DepartamentoRepository departamentoRepository;

	private final PaisRepository paisRepository;

	private final EstadoRepository estadoRepository;

	private final MunicipioRepository municipioRepository;

	public List<DepartamentoDTO> findAll() {
		return findAll(null, null, null, null, null);
	}

	public List<DepartamentoDTO> findAll(Long paisId, String nombre, Integer codigo, String acronimo, Long estadoId) {
		List<Departamento> departamentos = paisId == null
				? departamentoRepository.findAllByOrderByIdAsc()
				: departamentoRepository.findByPaisIdOrderByIdAsc(paisId);

		return departamentos.stream()
				.filter(departamento -> matchesText(departamento.getNombre(), nombre))
				.filter(departamento -> codigo == null || codigo.equals(departamento.getCodigo()))
				.filter(departamento -> matchesText(departamento.getAcronimo(), acronimo))
				.filter(departamento -> estadoId == null
						|| (departamento.getEstado() != null && estadoId.equals(departamento.getEstado().getId())))
			.map(departamentoMapper::toListDto)
				.collect(Collectors.toList());
	}

	public Optional<DepartamentoDTO> findById(Long requestedId) {
		return departamentoRepository.findById(requestedId).map(departamentoMapper::toListDto);
	}

	public DepartamentoDTO create(DepartamentoDTO departamentoDTO) {
		validateActiveCountry(departamentoDTO.getPaisId());
		validateGeneralStatus(departamentoDTO.getEstadoId(), "department.status.not.valid");
		validateUniqueFields(departamentoDTO, null);

		departamentoDTO.setId(null);

		return departamentoMapper.toDTO(departamentoRepository.save(departamentoMapper.toEntity(departamentoDTO)));
	}

	public void update(Long requestedId, DepartamentoDTO departamentoDTO) {
		Departamento existing = departamentoRepository.findById(requestedId)
			.orElseThrow(() -> new NotFoundException("department.not-found.with-id", requestedId));

		Pais pais = paisRepository.findById(departamentoDTO.getPaisId())
			.orElseThrow(() -> new BadRequestException("department.country.not.valid"));
		Estado estado = validateGeneralStatus(departamentoDTO.getEstadoId(), "department.status.not.valid");
		validateCanBeActive(departamentoDTO.getEstadoId(), pais);
		validateUniqueFields(departamentoDTO, existing.getId());

		departamentoDTO.setId(requestedId);

		departamentoRepository.save(departamentoMapper.toEntity(departamentoDTO));

		if (EstadoConstantes.ESTADO_GENERAL_INACTIVO.equals(estado.getId())) {
			inactivateMunicipalities(requestedId, estado);
		}
	}

	public void delete(Long id) {
		Departamento departamento = departamentoRepository.findById(id)
			.orElseThrow(() -> new NotFoundException("department.not-found.with-id", id));
		Estado inactiveStatus = getInactiveStatus();

		departamento.setEstado(inactiveStatus);
		departamentoRepository.save(departamento);
		inactivateMunicipalities(id, inactiveStatus);
	}

	private Pais validateActiveCountry(Long paisId) {
		Pais pais = paisRepository.findById(paisId)
			.orElseThrow(() -> new BadRequestException("department.country.not.valid"));

		if (pais.getEstado() == null || !EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(pais.getEstado().getId())) {
			throw new BadRequestException("department.country.must-be-active");
		}

		return pais;
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
			.orElseThrow(() -> new BadRequestException("department.status.not.valid"));
	}

	private void inactivateMunicipalities(Long departamentoId, Estado inactiveStatus) {
		List<Municipio> municipios = municipioRepository.findByDepartamentoIdOrderByIdAsc(departamentoId);
		municipios.forEach(municipio -> municipio.setEstado(inactiveStatus));
		municipioRepository.saveAll(municipios);
	}

	private void validateCanBeActive(Long estadoId, Pais pais) {
		if (EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(estadoId)
				&& (pais.getEstado() == null || !EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(pais.getEstado().getId()))) {
			throw new BadRequestException("department.country.must-be-active");
		}
	}

	private void validateUniqueFields(DepartamentoDTO departamentoDTO, Long currentId) {
		Long paisId = departamentoDTO.getPaisId();
		if (currentId == null) {
			if (departamentoRepository.existsByPaisIdAndNombreIgnoreCase(paisId, departamentoDTO.getNombre())) {
				throw new BadRequestException("department.name.duplicate");
			}
			if (departamentoRepository.existsByPaisIdAndCodigo(paisId, departamentoDTO.getCodigo())) {
				throw new BadRequestException("department.code.duplicate");
			}
			if (departamentoRepository.existsByPaisIdAndAcronimoIgnoreCase(paisId, departamentoDTO.getAcronimo())) {
				throw new BadRequestException("department.acronym.duplicate");
			}
			return;
		}

		if (departamentoRepository.existsByPaisIdAndNombreIgnoreCaseAndIdNot(paisId, departamentoDTO.getNombre(), currentId)) {
			throw new BadRequestException("department.name.duplicate");
		}
		if (departamentoRepository.existsByPaisIdAndCodigoAndIdNot(paisId, departamentoDTO.getCodigo(), currentId)) {
			throw new BadRequestException("department.code.duplicate");
		}
		if (departamentoRepository.existsByPaisIdAndAcronimoIgnoreCaseAndIdNot(paisId, departamentoDTO.getAcronimo(), currentId)) {
			throw new BadRequestException("department.acronym.duplicate");
		}
	}

	private boolean matchesText(String value, String filter) {
		return filter == null || filter.isBlank()
				|| (value != null && value.toLowerCase().contains(filter.trim().toLowerCase()));
	}

}
