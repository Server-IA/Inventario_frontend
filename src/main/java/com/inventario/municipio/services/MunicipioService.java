/*=============================================================================
 Nombre del archivo : MunicipioService.java
 Descripcion        : Servicio de negocio para operaciones de municipios.
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
package com.inventario.municipio.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.departamento.Departamento;
import com.inventario.departamento.repositories.DepartamentoRepository;
import com.inventario.estado.Estado;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.custom.BadRequestException;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.municipio.dtos.MunicipioDTO;
import com.inventario.municipio.mappers.MunicipioMapper;
import com.inventario.municipio.repositories.MunicipioRepository;
import com.inventario.validator.parametrizacion.constantes.EstadoConstantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MunicipioService {

	private final MunicipioMapper municipioMapper;

	private final MunicipioRepository municipioRepository;

	private final DepartamentoRepository departamentoRepository;

	private final EstadoRepository estadoRepository;

	@Transactional(readOnly = true)
	public List<MunicipioDTO> findAll(Long departamentoId, String nombre, Integer codigo, String acronimo, Long estadoId) {
		return municipioRepository
			.findByDepartamentoIdWithFilters(departamentoId, normalizeText(nombre), codigo, normalizeText(acronimo),
					estadoId)
			.stream()
			.map(municipioMapper::toListDto)
			.toList();
	}

	@Transactional(readOnly = true)
	public Optional<MunicipioDTO> findById(Long requestedId) {
		return municipioRepository.findById(requestedId).map(municipioMapper::toListDto);
	}

	@Transactional
	public MunicipioDTO create(MunicipioDTO municipioDTO) {
		validateActiveDepartment(municipioDTO.getDepartamentoId());
		validateGeneralStatus(municipioDTO.getEstadoId(), "municipality.status.not.valid");
		validateUniqueFields(municipioDTO, null);

		municipioDTO.setId(null);

		return municipioMapper.toDTO(municipioRepository.save(municipioMapper.toEntity(municipioDTO)));
	}

	@Transactional
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

	@Transactional
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

	private String normalizeText(String filter) {
		return filter == null ? null : filter.trim();
	}

}









