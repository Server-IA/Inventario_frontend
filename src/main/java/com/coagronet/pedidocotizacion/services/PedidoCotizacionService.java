package com.coagronet.pedidocotizacion.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedidocotizacion.PedidoCotizacion;
import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionRequestDTO;
import com.coagronet.pedidocotizacion.dtos.PedidoCotizacionResponseDTO;
import com.coagronet.pedidocotizacion.mappers.PedidoCotizacionMapper;
import com.coagronet.pedidocotizacion.repositories.PedidoCotizacionRepository;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PedidoCotizacionService {

	private final PedidoCotizacionRepository pedidoCotizacionRepository;

	private final PedidoCotizacionMapper pedidoCotizacionMapper;

	private final UserEmpresaService userEmpresaService;

	private final EntidadValidatorFacade entidadValidatorFacade;

	public Page<PedidoCotizacionResponseDTO> findAll(Pageable pageable) {
		return pedidoCotizacionRepository
			.findByPedidoEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable)
			.map(pedidoCotizacionMapper::toDTO);
	}

	public List<PedidoCotizacionResponseDTO> findAllByPedidoId(Long pedidoId) {
		return pedidoCotizacionRepository
			.findByPedidoIdAndPedidoEmpresaId(pedidoId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.stream()
			.map(pedidoCotizacionMapper::toDTO)
			.toList();
	}

	public PedidoCotizacionResponseDTO findById(Long requestedId) {
		return pedidoCotizacionRepository
			.findByIdAndPedidoEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(pedidoCotizacionMapper::toDTO)
			.orElseThrow(() -> new NotFoundException("pedido-cotizacion.not-found", requestedId));
	}

	@Transactional
	public PedidoCotizacionResponseDTO create(PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Pedido pedido = entidadValidatorFacade.validarPedido(pedidoCotizacionRequestDTO.pedidoId(), empresaId);
		Proveedor proveedor = entidadValidatorFacade.validarProveedor(pedidoCotizacionRequestDTO.proveedorId(),
				empresaId);
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(pedidoCotizacionRequestDTO.estadoId());

		PedidoCotizacion pedidoCotizacion = pedidoCotizacionMapper.toEntity(pedidoCotizacionRequestDTO);

		pedidoCotizacion.setPedido(pedido);
		pedidoCotizacion.setProveedor(proveedor);
		pedidoCotizacion.setEstado(estado);

		PedidoCotizacion savedPedidoCotizacion = pedidoCotizacionRepository.save(pedidoCotizacion);

		return pedidoCotizacionMapper.toDTO(savedPedidoCotizacion);

	}

	@Transactional
	public void update(Long requestedId, PedidoCotizacionRequestDTO pedidoCotizacionRequestDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		PedidoCotizacion pedidoCotizacion = entidadValidatorFacade.validarPedidoCotizacion(requestedId, empresaId);
		Pedido pedido = entidadValidatorFacade.validarPedido(pedidoCotizacionRequestDTO.pedidoId(), empresaId);
		Proveedor proveedor = entidadValidatorFacade.validarProveedor(pedidoCotizacionRequestDTO.proveedorId(),
				empresaId);
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(pedidoCotizacionRequestDTO.estadoId());

		pedidoCotizacion.setDescripcion(pedidoCotizacionRequestDTO.descripcion());
		pedidoCotizacion.setArchivo(pedidoCotizacionRequestDTO.archivo());
		pedidoCotizacion.setPedido(pedido);
		pedidoCotizacion.setProveedor(proveedor);
		pedidoCotizacion.setEstado(estado);
		pedidoCotizacionRepository.save(pedidoCotizacion);
	}

	@Transactional
	public void delete(Long id) {
		entidadValidatorFacade.validarPedidoCotizacion(id, userEmpresaService.getEmpresaIdFromCurrentRequest());

		pedidoCotizacionRepository.deleteById(id);
	}

}
