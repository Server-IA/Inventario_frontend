/*=============================================================================
 Nombre del archivo : DepartamentoService.java
 Descripcion        : Servicio de negocio para operaciones de departamentos.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2025-03-31 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 | 2026-05-29 | 1.2.0   | JUAN DIAZ            | Correcciones de cierre de PR: mejoras en filtros y consultas, ajustes en controladores y servicios, y migracion SQL de localizacion global. |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.departamento.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(readOnly = true)
	public List<DepartamentoDTO> findAll() {
		return findAll(null, null, null, null, null);
	}

	@Transactional(readOnly = true)
	public List<DepartamentoDTO> findAll(Long paisId, String nombre, Integer codigo, String acronimo, Long estadoId) {
		return departamentoRepository.findAllWithFilters(paisId, normalizeText(nombre), codigo, normalizeText(acronimo),
				estadoId)
				.stream()
				.map(departamentoMapper::toListDto)
				.toList();
	}

	@Transactional(readOnly = true)
	public Optional<DepartamentoDTO> findById(Long requestedId) {
		return departamentoRepository.findById(requestedId).map(departamentoMapper::toListDto);
	}

	@Transactional
	public DepartamentoDTO create(DepartamentoDTO departamentoDTO) {
		validateActiveCountry(departamentoDTO.getPaisId());
		validateGeneralStatus(departamentoDTO.getEstadoId(), "department.status.not.valid");
		validateUniqueFields(departamentoDTO, null);

		departamentoDTO.setId(null);

		return departamentoMapper.toDTO(departamentoRepository.save(departamentoMapper.toEntity(departamentoDTO)));
	}

	@Transactional
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

	@Transactional
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

	private String normalizeText(String filter) {
		return filter == null ? null : filter.trim();
	}

}









