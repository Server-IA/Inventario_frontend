package com.coagronet.articuloKardex.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.services.factory.ArticuloKardexFactory;
import com.coagronet.estado.Estado;
import com.coagronet.kardex.Kardex;
import com.coagronet.ordenCompra.services.OrdenCompraService;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.validator.EntidadValidatorFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;
import com.coagronet.articuloKardex.mappers.ArticuloKardexMapper;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticuloKardexService {

	private final UserEmpresaService userEmpresaService;
	private final ArticuloKardexMapper articuloKardexMapper;
	private final ArticuloKardexRepository articuloKardexRepository;
	private final KardexRepository kardexRepository;
	private final PresentacionProductoRepository presentacionProductoRepository;
	private final EstadoRepository estadoRepository;
	private final EntidadValidatorFacade entidadValidatorFacade;
	private final OrdenCompraService ordenCompraService;
	private final ArticuloKardexFactory articuloKardexFactory;

	public Page<ArticuloKardexDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return articuloKardexRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable)
			.map(articuloKardexMapper::toListDTO);
	}

	public List<ArticuloKardexDTO> findAllByKardexId(Long kardexId) {
		return articuloKardexRepository
			.findByEmpresaIdAndKardexIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), kardexId)
			.stream()
			.map(articuloKardexMapper::toListDTO)
			.collect(Collectors.toList());
	}

	public Optional<ArticuloKardexDTO> findById(Long requestedId) {
		return articuloKardexRepository
			.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(articuloKardexMapper::toListDTO);
	}

	@Transactional
	public ArticuloKardexDTO create(ArticuloKardexDTO articuloKardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Kardex kardex = entidadValidatorFacade.validarKardex(articuloKardexDTO.getKardexId(), empresaId);
		entidadValidatorFacade.validarEstadoGeneral(articuloKardexDTO.getEstadoId());
		entidadValidatorFacade.validarProductoPresentacion(articuloKardexDTO.getPresentacionProductoId(), empresaId);

		List<ArticuloKardex> guardados = articuloKardexFactory.crearArticulos(articuloKardexDTO, empresaId);

		Long ordenCompraId = (kardex.getOrdenCompra() != null) ? kardex.getOrdenCompra().getId() : null;
		ordenCompraService.validarEstadoDeEntrega(ordenCompraId, empresaId);

		return articuloKardexMapper.toDTO(guardados.getLast());
	}


	@Transactional
	public void update(Long requestedId, ArticuloKardexDTO articuloKardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		ArticuloKardex articuloKardexExistente = entidadValidatorFacade.validarArticuloKardex(requestedId, empresaId);

		Kardex kardex = entidadValidatorFacade.validarKardex(articuloKardexDTO.getKardexId(), empresaId);

		PresentacionProducto presentacion =entidadValidatorFacade.validarProductoPresentacion(articuloKardexDTO.getPresentacionProductoId(), empresaId);

		Estado estado = entidadValidatorFacade.validarEstadoGeneral(articuloKardexDTO.getEstadoId());

		articuloKardexExistente.setKardex(kardex);
		articuloKardexExistente.setPresentacionProducto(presentacion);
		articuloKardexExistente.setEstado(estado);

		articuloKardexRepository.save(articuloKardexExistente);
	}

	public void delete(Long id) {
		articuloKardexRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El artículo de kardex no fue encontrado."));

		articuloKardexRepository.deleteById(id);
	}

}
