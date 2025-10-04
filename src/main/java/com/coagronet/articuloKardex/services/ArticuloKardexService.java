package com.coagronet.articuloKardex.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.coagronet.articuloKardex.ArticuloKardex;
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

		List<ArticuloKardex> guardados;

		if (esDesgregado(articuloKardexDTO, empresaId)) {
			guardados = crearArticulosDesgregados(articuloKardexDTO, empresaId);
		} else {
			articuloKardexDTO.setEmpresaId(empresaId);
			ArticuloKardex entidad = articuloKardexMapper.toEntity(articuloKardexDTO);
			guardados = List.of(articuloKardexRepository.save(entidad));
		}
		Long ordenCompraId = (kardex.getOrdenCompra() != null) ? kardex.getOrdenCompra().getId() : null;
		ordenCompraService.validarEstadoDeEntrega(ordenCompraId, empresaId);

		return articuloKardexMapper.toDTO(guardados.getLast());
	}

	private boolean esDesgregado(ArticuloKardexDTO dto, Long empresaId) {
		return presentacionProductoRepository.findByIdAndEmpresaId(dto.getPresentacionProductoId(), empresaId)
				.map(PresentacionProducto::getDesgregar)
				.orElse(false);
	}

	private List<ArticuloKardex> crearArticulosDesgregados(ArticuloKardexDTO dto, Long empresaId) {
		double cantidad = dto.getCantidad();
		long unidades = Math.round(cantidad);

		if (Math.abs(cantidad - unidades) > 1e-9) {
			throw new BadRequestException("Para presentaciones desgregadas, la cantidad debe ser un número entero.");
		}

		List<ArticuloKardex> creados = new ArrayList<>();
		for (int i = 0; i < unidades; i++) {
			ArticuloKardexDTO item = construirArticuloKardexUnitario(dto, empresaId);
			ArticuloKardex entidad = articuloKardexMapper.toEntity(item);
			creados.add(articuloKardexRepository.save(entidad));
		}
		return creados;
	}

	private static ArticuloKardexDTO construirArticuloKardexUnitario(ArticuloKardexDTO articuloKardexDTO, Long empresaId) {
		ArticuloKardexDTO item = new ArticuloKardexDTO();
		item.setEmpresaId(empresaId);
		item.setKardexId(articuloKardexDTO.getKardexId());
		item.setPresentacionProductoId(articuloKardexDTO.getPresentacionProductoId());
		item.setEstadoId(articuloKardexDTO.getEstadoId());

		item.setCantidad(1.0);

		item.setPrecio(articuloKardexDTO.getPrecio());
		item.setFechaVencimiento(articuloKardexDTO.getFechaVencimiento());
		item.setIdentificadorProducto(articuloKardexDTO.getIdentificadorProducto());
		item.setLote(articuloKardexDTO.getLote());
		return item;
	}

	public void update(Long requestedId, ArticuloKardexDTO articuloKardexDTO) {
		articuloKardexRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El artículo de kardex no fue encontrado."));

		kardexRepository
			.findByIdAndEmpresaId(articuloKardexDTO.getKardexId(), userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("El kardex no es válido."));

		presentacionProductoRepository
			.findByIdAndEmpresaId(articuloKardexDTO.getPresentacionProductoId(),
					userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new BadRequestException("La presentación de producto no es válida."));

		estadoRepository.findById(articuloKardexDTO.getEstadoId())
			.orElseThrow(() -> new BadRequestException("El estado no es válido."));

		articuloKardexDTO.setId(requestedId);
		articuloKardexDTO.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		articuloKardexRepository.save(articuloKardexMapper.toEntity(articuloKardexDTO));
	}

	public void delete(Long id) {
		articuloKardexRepository.findByIdAndEmpresaId(id, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("El artículo de kardex no fue encontrado."));

		articuloKardexRepository.deleteById(id);
	}

}
