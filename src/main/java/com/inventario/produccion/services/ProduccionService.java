package com.inventario.produccion.services;

import java.util.Optional;

import com.inventario.empresa.Empresa;
import com.inventario.espacio.Espacio;
import com.inventario.estado.Estado;
import com.inventario.produccion.Produccion;
import com.inventario.produccion.dtos.ProduccionCreateDTO;
import com.inventario.subseccion.Subseccion;
import com.inventario.tipoProduccion.TipoProduccion;
import com.inventario.validator.EntidadValidatorFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.produccion.dtos.ProduccionDTO;
import com.inventario.produccion.mappers.ProduccionMapper;
import com.inventario.produccion.repositories.ProduccionRepository;
import com.inventario.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProduccionService {

	private final ProduccionRepository produccionRepository;
	private final ProduccionMapper produccionMapper;
	private final UserEmpresaService userEmpresaService;
	private final EntidadValidatorFacade entidadValidatorFacade;

	/* ---------- READ ---------- */

	public Page<ProduccionDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return produccionRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(produccionMapper::toDto);
	}

	public Optional<ProduccionDTO> findById(Long requestedId) {
		return produccionRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(produccionMapper::toDto);
	}

	/* ---------- CREATE ---------- */

	@Transactional
	public ProduccionDTO create(ProduccionCreateDTO produccionDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Empresa empresa = entidadValidatorFacade.validarEmpresa(empresaId);
		TipoProduccion tipoProduccion = entidadValidatorFacade.validarTipoProduccion(produccionDTO.getTipoProduccionId(), empresaId);
		Espacio espacio = entidadValidatorFacade.validarEspacio(produccionDTO.getEspacioId(), empresaId);
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(produccionDTO.getEstadoId());
		Subseccion subseccion = entidadValidatorFacade.validarSubseccion(produccionDTO.getSubSeccionId(), empresaId);

		Produccion produccion = produccionMapper.toEntity(produccionDTO);
		entidadValidatorFacade.validarFechasProduccion(produccion);
		produccion.setEmpresa(empresa);
		produccion.setTipoProduccion(tipoProduccion);
		produccion.setEspacio(espacio);
		produccion.setEstado(estado);
		produccion.setSubSeccion(subseccion);

		Produccion guardado = produccionRepository.save(produccion);
		return produccionMapper.toDto(guardado);
	}

	/* ---------- UPDATE ---------- */

	@Transactional
	public void update(Long requestedId, ProduccionDTO produccionDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Produccion produccionActual = entidadValidatorFacade.validarProduccion(requestedId, empresaId);
		TipoProduccion tipoProduccion = entidadValidatorFacade.validarTipoProduccion(produccionDTO.getTipoProduccionId(), empresaId);
		Espacio espacio = entidadValidatorFacade.validarEspacio(produccionDTO.getEspacioId(), empresaId);
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(produccionDTO.getEstadoId());
		Subseccion subseccion = entidadValidatorFacade.validarSubseccion(produccionDTO.getSubSeccionId(), empresaId);

		produccionMapper.updateEntityFromDto(produccionDTO, produccionActual);

		produccionActual.setTipoProduccion(tipoProduccion);
		produccionActual.setEspacio(espacio);
		produccionActual.setEstado(estado);
		produccionActual.setSubSeccion(subseccion);

		entidadValidatorFacade.validarFechasProduccion(produccionActual);
		produccionRepository.save(produccionActual);
	}

	/* ---------- DELETE ---------- */

	@Transactional
	public void delete(Long requestId) {
		produccionRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Produccion not found"));

		produccionRepository.deleteById(requestId);
	}

}
