package com.inventario.articuloKardex.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.articuloKardex.ArticuloKardex;
import com.inventario.articuloKardex.dtos.ArticuloKardexDTO;
import com.inventario.articuloKardex.dtos.KardexItemResponseDto;
import com.inventario.articuloKardex.mappers.ArticuloKardexMapper;
import com.inventario.articuloKardex.repositories.ArticuloKardexRepository;
import com.inventario.articuloKardex.services.factory.ArticuloKardexFactory;
import com.inventario.estado.repositories.EstadoRepository;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.kardex.Kardex;
import com.inventario.kardex.repositories.KardexRepository;
import com.inventario.ordenCompra.services.OrdenCompraService;
import com.inventario.presentacionProducto.repositories.PresentacionProductoRepository;
import com.inventario.utils.UserEmpresaService;
import com.inventario.validator.EntidadValidatorFacade;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArticuloKardexService {

	private final UserEmpresaService userEmpresaService;

	private final ArticuloKardexMapper articuloKardexMapper;

	private final ArticuloKardexRepository articuloKardexRepository;

	private final EntidadValidatorFacade entidadValidatorFacade;

	private final OrdenCompraService ordenCompraService;

	private final ArticuloKardexFactory articuloKardexFactory;

	private final KardexRepository kardexRepository;

	private final PresentacionProductoRepository presentacionProductoRepository;

	private final EstadoRepository estadoRepository;

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
	public ArticuloKardexDTO create(ArticuloKardexDTO articuloKardexDTO, HttpServletRequest request) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Kardex kardex = kardexRepository.findById(articuloKardexDTO.getKardexId()).orElseThrow();
		entidadValidatorFacade.validarEstadoGeneral(articuloKardexDTO.getEstadoId());
		entidadValidatorFacade.validarProductoPresentacion(articuloKardexDTO.getPresentacionProductoId(), empresaId);

		List<ArticuloKardex> guardados = articuloKardexFactory.crearArticulos(articuloKardexDTO, empresaId, request);

		Long ordenCompraId = (kardex.getOrdenCompra() != null) ? kardex.getOrdenCompra().getId() : null;
		ordenCompraService.validarEstadoDeEntrega(ordenCompraId, empresaId);

		return articuloKardexMapper.toDTO(guardados.getLast());
	}

	@Transactional
	public void update(Long requestedId, ArticuloKardexDTO dto) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		ArticuloKardex articuloExistente = articuloKardexRepository.findByIdAndEmpresaId(requestedId, empresaId)
			.orElseThrow(() -> new NotFoundException("El art?culo de kardex no fue encontrado."));

		articuloKardexMapper.updateEntityFromDto(dto, articuloExistente);

		articuloExistente.setKardex(kardexRepository.findById(dto.getKardexId()).orElseThrow());
		articuloExistente.setPresentacionProducto(presentacionProductoRepository
			.getReferenceByIdAndEmpresaId(dto.getPresentacionProductoId(), empresaId));
		articuloExistente.setEstado(estadoRepository.getReferenceById(dto.getEstadoId()));

		articuloKardexRepository.save(articuloExistente);
	}

	@Transactional
	public void delete(Long id) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		int eliminados = articuloKardexRepository.softDeleteByIdAndEmpresaId(id, empresaId);

		if (eliminados == 0) {
			throw new NotFoundException("El art?culo de kardex no fue encontrado.");
		}
	}

	@Transactional(readOnly = true)
	public Page<KardexItemResponseDto> getKardexItems(Long kardexId, Long productoId, Long estadoId,
			LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {

		return articuloKardexRepository.findItemsByKardexIdWithFilters(kardexId, productoId, estadoId, fechaInicio,
				fechaFin, pageable);
	}

}
