package com.coagronet.ordenCompra.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.articuloOrdenCompra.ArticuloOrdenCompra;
import com.coagronet.articuloOrdenCompra.repositories.ArticuloOrdenCompraRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.constantes.OrdenCompraConstantes;
import com.coagronet.ordenCompra.constantes.PedidoConstantes;
import com.coagronet.ordenCompra.dtos.OrdenCompraCreateDTO;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;
import com.coagronet.ordenCompra.dtos.OrdenCompraLookupDTO;
import com.coagronet.ordenCompra.mappers.OrdenCompraMapper;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrdenCompraService {

	private final OrdenCompraRepository ordenCompraRepository;

	private final PedidoRepository pedidoRepository;

	private final OrdenCompraMapper ordenCompraMapper;

	private final UserEmpresaService userEmpresaService;

	private final EntidadValidatorFacade entidadValidatorFacade;

	private final ArticuloOrdenCompraRepository articuloOrdenCompraRepository;

	private final ArticuloKardexRepository articuloKardexRepository;

	private final OrdenCompraEstadoCalculator ordenCompraEstadoCalculator;

	public Page<OrdenCompraDTO> findAll(Pageable pageable) {
		return ordenCompraRepository
				.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable)
				.map(ordenCompraMapper::toDTO);
	}

	public OrdenCompraDTO findById(Long requestedId) {
		return ordenCompraRepository
				.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(ordenCompraMapper::toDTO)
				.orElseThrow(() -> new NotFoundException("orden-compra.not-found", requestedId));
	}

	@Transactional
	public OrdenCompraDTO create(OrdenCompraCreateDTO ordenCompraDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		ordenCompraDTO.setEmpresaId(empresaId);

		Pedido pedido = entidadValidatorFacade.validarPedido(ordenCompraDTO.getPedidoId(), empresaId);
		Proveedor proveedor = entidadValidatorFacade.validarProveedor(ordenCompraDTO.getProveedorId(), empresaId);
		Estado estadoInicial = entidadValidatorFacade
				.validarEstadoParaOrdenCompra(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_INICIAL_ACTIVO);

		OrdenCompra ordenCompra = ordenCompraMapper.toEntity(ordenCompraDTO);

		ordenCompra.setEmpresa(Empresa.builder().id(empresaId).build());
		ordenCompra.setPedido(pedido);
		ordenCompra.setProveedor(proveedor);
		ordenCompra.setEstado(estadoInicial);

		OrdenCompra guardado = ordenCompraRepository.save(ordenCompra);

		return ordenCompraMapper.toDTO(guardado);

	}

	@Transactional
	public void update(Long requestedId, OrdenCompraDTO ordenCompraDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		OrdenCompra ordenCompra = entidadValidatorFacade.validarOrdenCompra(requestedId, empresaId);
		Pedido pedido = entidadValidatorFacade.validarPedido(ordenCompraDTO.getPedidoId(), empresaId);
		Proveedor proveedor = entidadValidatorFacade.validarProveedor(ordenCompraDTO.getProveedorId(), empresaId);
		Estado estado = entidadValidatorFacade.validarEstadoParaOrdenCompra(ordenCompraDTO.getEstadoId());

		ordenCompra.setDescripcion(ordenCompraDTO.getDescripcion());
		ordenCompra.setFechaHora(ordenCompraDTO.getFechaHora());
		ordenCompra.setPedido(pedido);
		ordenCompra.setProveedor(proveedor);
		ordenCompra.setEstado(estado);
		ordenCompraRepository.save(ordenCompra);
	}

	@Transactional
	public void delete(Long requestedId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		entidadValidatorFacade.validarOrdenCompra(requestedId, empresaId);

		ordenCompraRepository.deleteById(requestedId);
	}

	@Transactional
	public void enviarOrdenCompraAlProveedor(Long ordenCompraId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		OrdenCompra ordenCompra = entidadValidatorFacade.validarOrdenCompra(ordenCompraId, empresaId);
		Estado nuevoEstadoOrdenCompra = entidadValidatorFacade
				.validarEstadoParaOrdenCompra(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_ENTREGADO_AL_PROVEEDOR);
		if (!ordenCompra.getEstado().getId().equals(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_INICIAL_ACTIVO)) {
			throw new BadRequestException("Solo las ordenes de compra con estado activo pueden enviarse al proveedor");
		}

		ordenCompra.setEstado(nuevoEstadoOrdenCompra);
		Pedido pedido = ordenCompra.getPedido();
		Estado nuevoEstadoPedido = entidadValidatorFacade
				.validarEstadoParaPedido(PedidoConstantes.ESTADO_CON_ORDEN_COMPRA);
		pedido.setEstado(nuevoEstadoPedido);
		pedidoRepository.save(pedido);

	}

	@Transactional
	public void validarEstadoDeEntrega(Long ordenCompraId, Long empresaId) {
		if (ordenCompraId == null) {
			log.info("⏭️ Kardex sin orden de compra asociada, no se valida estado de entrega.");
			return;
		}

		OrdenCompra ordenCompra = entidadValidatorFacade.validarOrdenCompra(ordenCompraId, empresaId);

		List<ArticuloOrdenCompra> articulosOC = articuloOrdenCompraRepository
				.findByEmpresaIdAndOrdenCompraIdOrderByIdAsc(empresaId, ordenCompraId);

		List<ArticuloKardex> articulosKardex = articuloKardexRepository
				.findByEmpresaIdAndKardex_OrdenCompra_IdOrderByIdAsc(empresaId, ordenCompraId);

		Estado nuevoEstado = ordenCompraEstadoCalculator.calcularNuevoEstado(articulosOC, articulosKardex);

		if (nuevoEstado != null && !ordenCompra.getEstado().equals(nuevoEstado)) {
			ordenCompra.setEstado(nuevoEstado);
			ordenCompraRepository.save(ordenCompra);
			log.info("✅ Estado de OC actualizado a {}", nuevoEstado.getId());
		}
	}

	@Transactional(readOnly = true)
	public List<OrdenCompraLookupDTO> listarParaSeleccion(Long pedidoId, Long empresaId) {
		return ordenCompraRepository.findLookupByPedidoAndEmpresaAndEstadoId(pedidoId, empresaId);
	}

}
