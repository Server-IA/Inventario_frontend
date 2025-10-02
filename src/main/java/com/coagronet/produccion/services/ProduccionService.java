package com.coagronet.produccion.services;

import java.util.Optional;

import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
import com.coagronet.estado.Estado;
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.dtos.ProduccionCreateDTO;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.validator.EntidadValidatorFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.produccion.dtos.ProduccionDTO;
import com.coagronet.produccion.mappers.ProduccionMapper;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.utils.UserEmpresaService;

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
