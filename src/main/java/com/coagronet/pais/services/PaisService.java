/*=============================================================================
 Nombre del archivo : PaisService.java
 Descripcion        : Servicio de negocio para operaciones de paises.
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
package com.coagronet.pais.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.departamento.Departamento;
import com.coagronet.departamento.repositories.DepartamentoRepository;
import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.municipio.Municipio;
import com.coagronet.municipio.repositories.MunicipioRepository;
import com.coagronet.pais.Pais;
import com.coagronet.pais.dtos.PaisDTO;
import com.coagronet.pais.mappers.PaisMapper;
import com.coagronet.pais.repositories.PaisRepository;
import com.coagronet.validator.parametrizacion.constantes.EstadoConstantes;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaisService {

	private final PaisMapper paisMapper;

	private final PaisRepository paisRepository;

	private final EstadoRepository estadoRepository;

	private final DepartamentoRepository departamentoRepository;

	private final MunicipioRepository municipioRepository;

	@Transactional(readOnly = true)
	public List<PaisDTO> findAll() {
		return paisRepository.findAllByOrderByIdAsc()
				.stream()
				.map(paisMapper::toListDto)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Optional<PaisDTO> findById(Long requestedId) {
		return paisRepository.findById(requestedId).map(paisMapper::toListDto);
	}

	@Transactional
	public PaisDTO create(PaisDTO paisDTO) {
		validateGeneralStatus(paisDTO.getEstadoId());
		validateUniqueFields(paisDTO, null);

		paisDTO.setId(null);

		return paisMapper.toDTO(paisRepository.save(paisMapper.toEntity(paisDTO)));
	}

	@Transactional
	public void update(Long requestedId, PaisDTO paisDTO) {
		paisRepository.findById(requestedId)
				.orElseThrow(() -> new NotFoundException("country.not-found.with-id", requestedId));

		Estado estado = validateGeneralStatus(paisDTO.getEstadoId());
		validateUniqueFields(paisDTO, requestedId);

		paisDTO.setId(requestedId);

		paisRepository.save(paisMapper.toEntity(paisDTO));

		if (EstadoConstantes.ESTADO_GENERAL_INACTIVO.equals(estado.getId())) {
			inactivateChildren(requestedId, estado);
		}
	}

	@Transactional
	public void delete(Long id) {
		Pais pais = paisRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("country.not-found.with-id", id));
		Estado inactiveStatus = getInactiveStatus();

		pais.setEstado(inactiveStatus);
		paisRepository.save(pais);
		inactivateChildren(id, inactiveStatus);
	}

	private Estado validateGeneralStatus(Long estadoId) {
		Estado estado = estadoRepository.findById(estadoId)
				.orElseThrow(() -> new BadRequestException("country.status.not.valid"));

		if (!EstadoConstantes.ESTADO_GENERAL_ACTIVO.equals(estado.getId())
				&& !EstadoConstantes.ESTADO_GENERAL_INACTIVO.equals(estado.getId())) {
			throw new BadRequestException("country.status.not.valid");
		}

		return estado;
	}

	private Estado getInactiveStatus() {
		return estadoRepository.findById(EstadoConstantes.ESTADO_GENERAL_INACTIVO)
				.orElseThrow(() -> new BadRequestException("country.status.not.valid"));
	}

	private void inactivateChildren(Long paisId, Estado inactiveStatus) {
		List<Departamento> departamentos = departamentoRepository.findByPaisIdOrderByIdAsc(paisId);
		for (Departamento departamento : departamentos) {
			departamento.setEstado(inactiveStatus);
			List<Municipio> municipios = municipioRepository.findByDepartamentoIdOrderByIdAsc(departamento.getId());
			municipios.forEach(municipio -> municipio.setEstado(inactiveStatus));
			municipioRepository.saveAll(municipios);
		}
		departamentoRepository.saveAll(departamentos);
	}

	private void validateUniqueFields(PaisDTO paisDTO, Long currentId) {
		if (currentId == null) {
			if (paisRepository.existsByNombreIgnoreCase(paisDTO.getNombre())) {
				throw new BadRequestException("country.name.duplicate");
			}
			if (paisRepository.existsByCodigo(paisDTO.getCodigo())) {
				throw new BadRequestException("country.code.duplicate");
			}
			if (paisRepository.existsByAcronimoIgnoreCase(paisDTO.getAcronimo())) {
				throw new BadRequestException("country.acronym.duplicate");
			}
			return;
		}

		if (paisRepository.existsByNombreIgnoreCaseAndIdNot(paisDTO.getNombre(), currentId)) {
			throw new BadRequestException("country.name.duplicate");
		}
		if (paisRepository.existsByCodigoAndIdNot(paisDTO.getCodigo(), currentId)) {
			throw new BadRequestException("country.code.duplicate");
		}
		if (paisRepository.existsByAcronimoIgnoreCaseAndIdNot(paisDTO.getAcronimo(), currentId)) {
			throw new BadRequestException("country.acronym.duplicate");
		}
	}

}
