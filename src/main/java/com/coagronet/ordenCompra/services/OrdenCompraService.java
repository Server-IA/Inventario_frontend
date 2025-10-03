package com.coagronet.ordenCompra.services;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.BadRequestException;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.constantes.OrdenCompraConstantes;
import com.coagronet.ordenCompra.constantes.PedidoConstantes;
import com.coagronet.ordenCompra.dtos.OrdenCompraCreateDTO;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.validator.EntidadValidatorFacade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.ordenCompra.dtos.OrdenCompraDTO;
import com.coagronet.ordenCompra.mappers.OrdenCompraMapper;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

	private final OrdenCompraRepository ordenCompraRepository;
	private final PedidoRepository pedidoRepository;

	private final OrdenCompraMapper ordenCompraMapper;

	private final UserEmpresaService userEmpresaService;
	private final EntidadValidatorFacade entidadValidatorFacade;

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
		Estado estadoInicial = entidadValidatorFacade.validarEstadoParaOrdenCompra(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_INICIAL_ACTIVO);

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
		Long empresaId =  userEmpresaService.getEmpresaIdFromCurrentRequest();
		entidadValidatorFacade.validarOrdenCompra(requestedId, empresaId);

		ordenCompraRepository.deleteById(requestedId);
	}

	@Transactional
	public void enviarOrdenCompraAlProveedor(Long ordenCompraId){
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		OrdenCompra ordenCompra = entidadValidatorFacade.validarOrdenCompra(ordenCompraId, empresaId);
		Estado nuevoEstadoOrdenCompra = entidadValidatorFacade.validarEstadoParaOrdenCompra(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_ENTREGADO_AL_PROVEEDOR);
		if(!ordenCompra.getEstado().getId().equals(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_INICIAL_ACTIVO)){
			throw new BadRequestException("Solo las ordenes de compra con estado activo pueden enviarse al proveedor");
		}

		ordenCompra.setEstado(nuevoEstadoOrdenCompra);
		Pedido pedido = ordenCompra.getPedido();
		Estado nuevoEstadoPedido = entidadValidatorFacade.validarEstadoParaPedido(PedidoConstantes.ESTADO_CON_ORDEN_COMPRA);
		pedido.setEstado(nuevoEstadoPedido);
		pedidoRepository.save(pedido);

	}

	@Transactional
	public void entregaParcial(Long ordenCompraId){
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		OrdenCompra ordenCompra = entidadValidatorFacade.validarOrdenCompra(ordenCompraId, empresaId);
		Estado nuevoEstadoOrdenCompra = entidadValidatorFacade.validarEstadoParaOrdenCompra(OrdenCompraConstantes.ESTADO_ORDEN_COMPRA_ENTREGA_PARCIAL);


		
	}

}
