package com.coagronet.pedido.services;

import static com.coagronet.pedido.estados.PedidoEstadoConst.ACTIVO;
import static com.coagronet.pedido.estados.PedidoEstadoConst.ANULADO;
import static com.coagronet.pedido.estados.PedidoEstadoConst.COMPLETADO;
import static com.coagronet.pedido.estados.PedidoEstadoConst.CON_ORDEN_COMPRA;
import static com.coagronet.pedido.estados.PedidoEstadoConst.PENDIENTE_COTIZACION;

import java.util.Arrays;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.estado.support.EstadoResolver;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.exceptionHandler.custom.BadRequestException;
import com.coagronet.inventario.gateway.InventarioGateway;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.dtos.PedidoDTO;
import com.coagronet.pedido.mappers.PedidoMapper;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.utils.UserEmpresaService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	private final PedidoMapper pedidoMapper;

	private final AlmacenRepository almacenRepository;

	private final ProduccionRepository produccionRepository;

	private final EstadoResolver estadoResolver;

	private final UserEmpresaService userEmpresaService;

	private final InventarioGateway inventarioGateway;

	/* ================== Helpers de estado ================== */

	private Long estadoId(String acr) {
		return estadoResolver.idPedido(acr);
	}

	private Pedido getByIdAndEmpresaOrThrow(Long id) {
		Long empId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return pedidoRepository.findByIdAndEmpresaId(id, empId)
				.orElseThrow(() -> new NotFoundException("pedido.not-found", id));
	}

	private void ensureTransition(Pedido p, String targetAcr, String... allowedFromAcrs) {
		Long currentId = p.getEstado().getId();
		Long targetId = estadoId(targetAcr);
		Set<Long> allowed = Arrays.stream(allowedFromAcrs)
				.map(this::estadoId)
				.collect(java.util.stream.Collectors.toSet());
		if (!allowed.contains(currentId)) {
			throw new BadRequestException("pedido.estado.transition.not-allowed " + currentId + " a " + targetId
					+ " (target=" + targetAcr
					+ ", allowedFrom=" + Arrays.toString(allowedFromAcrs) + ")");
		}
	}

	private void setEstado(Pedido p, String acr) {
		Long tgtId = estadoId(acr);
		if (p.getEstado() == null || !tgtId.equals(p.getEstado().getId())) {
			p.setEstado(estadoResolver.getRef(tgtId));
		}
	}

	/* ================== CRUD b?sico ================== */

	public Page<PedidoDTO> findAll(Pageable pageable) {
		return pedidoRepository
				.findByEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable)
				.map(pedidoMapper::toDto);
	}

	public PedidoDTO findById(Long requestedId) {
		return pedidoRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(pedidoMapper::toDto)
				.orElseThrow(() -> new NotFoundException("pedido.not-found", requestedId));
	}

	@Transactional
	public Long create(PedidoDTO dto) {
		if (dto.getAlmacenId() != null) {
			almacenRepository.findById(dto.getAlmacenId())
					.orElseThrow(() -> new NotFoundException("almacen.not-found", dto.getAlmacenId()));
		}
		if (dto.getProduccionId() != null) {
			produccionRepository.findById(dto.getProduccionId())
					.orElseThrow(() -> new NotFoundException("produccion.not-found", dto.getProduccionId()));
		}

		dto.setEmpresaId(userEmpresaService.getEmpresaIdFromCurrentRequest());

		if (dto.getEstadoId() == null) {
			dto.setEstadoId(estadoId(ACTIVO));
		}

		Pedido entity = pedidoMapper.toEntity(dto);
		Pedido saved = pedidoRepository.save(entity);
		return saved.getId();
	}

	@Transactional
	public void update(Long id, PedidoDTO dto) {
		Pedido current = getByIdAndEmpresaOrThrow(id);
		dto.setId(current.getId());
		dto.setEmpresaId(current.getEmpresa().getId());
		dto.setEstadoId(current.getEstado().getId());

		pedidoRepository.save(pedidoMapper.toEntity(dto));
	}

	@Transactional
	public void delete(Long id) {
		getByIdAndEmpresaOrThrow(id);
		pedidoRepository.deleteById(id);
	}

	/* ================== Reglas de negocio / Transiciones ================== */

	@Transactional
	public void marcarRequiereCotizacion(Long pedidoId) {
		Pedido p = getByIdAndEmpresaOrThrow(pedidoId);
		ensureTransition(p, PENDIENTE_COTIZACION, ACTIVO);
		setEstado(p, PENDIENTE_COTIZACION);
		// JPA sincroniza al finalizar la transacci?n
	}

	@Transactional
	public void anular(Long pedidoId) {
		Pedido p = getByIdAndEmpresaOrThrow(pedidoId);
		ensureTransition(p, ANULADO, ACTIVO, PENDIENTE_COTIZACION, CON_ORDEN_COMPRA);
		setEstado(p, ANULADO);
	}

	@Transactional
	public void completar(Long pedidoId) {
		Pedido p = getByIdAndEmpresaOrThrow(pedidoId);

		var res = inventarioGateway.validarRequisitosParaCompletar(pedidoId);
		if (!res.isOk()) {
			String motivo = res.getMotivoFallo() != null ? res.getMotivoFallo() : "Aún hay ítems pendientes por recibir/verificar/registrar en inventario.";
			throw new BadRequestException("pedido.completar.requisitos-no-cumplidos: " + motivo);
		}

		ensureTransition(p, COMPLETADO, CON_ORDEN_COMPRA);
		setEstado(p, COMPLETADO); // ahora reemplaza la ref, no el id
	}

}
