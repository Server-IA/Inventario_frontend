package com.coagronet.articuloKardex.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.dtos.ArticuloKardexDTO;
import com.coagronet.articuloKardex.mappers.ArticuloKardexMapper;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.articuloKardex.services.factory.ArticuloKardexFactory;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.ordenCompra.services.OrdenCompraService;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;

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

		Kardex kardex = entidadValidatorFacade.obtenerParaMutacion(articuloKardexDTO.getKardexId(), empresaId);
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
			.orElseThrow(() -> new NotFoundException("El artículo de kardex no fue encontrado."));

		articuloKardexMapper.updateEntityFromDto(dto, articuloExistente);

		articuloExistente.setKardex(kardexRepository.getReferenceByIdAndEmpresaId(dto.getKardexId(), empresaId));
		articuloExistente.setPresentacionProducto(presentacionProductoRepository
			.getReferenceByIdAndEmpresaId(dto.getPresentacionProductoId(), empresaId));
		articuloExistente.setEstado(estadoRepository.getReferenceById(dto.getEstadoId()));

		articuloKardexRepository.save(articuloExistente);
	}

	@Transactional
	public void delete(Long id) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		// Usando @Modifying en el repositorio:
		// @Modifying @Query("DELETE FROM ArticuloKardex a WHERE a.id = :id AND
		// a.empresa.id = :empresaId")
		int eliminados = articuloKardexRepository.deleteByIdAndEmpresaId(id, empresaId);

		if (eliminados == 0) {
			throw new NotFoundException("El artículo de kardex no fue encontrado.");
		}
	}

}
