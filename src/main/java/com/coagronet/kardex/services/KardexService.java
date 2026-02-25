package com.coagronet.kardex.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.almacen.Almacen;
import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.auditoria.RequestUtils;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.pedido.Pedido;
import com.coagronet.produccion.Produccion;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KardexService {

	private final KardexRepository kardexRepository;

	private final KardexMapper kardexMapper;

	private final UserEmpresaService userEmpresaService;

	private final EntidadValidatorFacade entidadValidatorFacade;

	private final RequestUtils requestUtils;

	private final AuthenticationService authenticationService;

	private final HttpServletRequest request;

	public Page<KardexDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return kardexRepository.findDtoByEmpresaIdOrderByIdAsc(empresaId, pageable);
	}

	public Optional<KardexDTO> findById(Long requestedId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return kardexRepository.findDtoByIdAndEmpresaId(requestedId, empresaId);
	}

	@Transactional
	public KardexDTO create(KardexDTO kardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		kardexDTO.setEmpresaId(empresaId);

		Kardex kardex = kardexMapper.toEntity(kardexDTO);

		aplicarValidacionesYRelaciones(kardexDTO, kardex, empresaId);
		Kardex guardado = kardexRepository.save(kardex);
		return kardexMapper.toDto(guardado);
	}

	@Transactional
	public KardexDTO update(Long requestedId, KardexDTO kardexDTO) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		Kardex kardexExistente = entidadValidatorFacade.obtenerParaMutacion(requestedId, empresaId);
		kardexMapper.updateEntityFromDto(kardexDTO, kardexExistente);

		aplicarValidacionesYRelaciones(kardexDTO, kardexExistente, empresaId);

		Kardex guardado = kardexRepository.save(kardexExistente);
		return kardexMapper.toDto(guardado);
	}

	@Transactional
	public void inactivar(Long requestId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		int updatedCount = kardexRepository.inactivarByIdAndEmpresaId(requestId, empresaId);

		if (updatedCount == 0) {
			throw new RecursoNoEncontradoException("Kardex", requestId);
		}
	}

	private void aplicarValidacionesYRelaciones(KardexDTO kardexDTO, Kardex kardex, Long empresaId) {
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(kardexDTO.getEstadoId());
		Almacen almacen = entidadValidatorFacade.validarAlmacen(kardexDTO.getAlmacenId(), empresaId);
		Produccion produccion = entidadValidatorFacade.validarProduccion(kardexDTO.getProduccionId(), empresaId);
		TipoMovimiento tipoMovimiento = entidadValidatorFacade.validarTipoMovimiento(kardexDTO.getTipoMovimientoId(),
				empresaId);
		Pedido pedido = null;
		if (kardexDTO.getPedidoId() != null) {
			pedido = entidadValidatorFacade.validarPedido(kardexDTO.getPedidoId(), empresaId);
		}

		OrdenCompra ordenCompra = null;
		if (kardexDTO.getOrdenCompraId() != null) {
			ordenCompra = entidadValidatorFacade.validarOrdenCompra(kardexDTO.getOrdenCompraId(), empresaId);
		}

		if (kardexDTO.getClienteProveedorId() != null) {
			Empresa clienteProveedor = entidadValidatorFacade
				.validarClienteProveedor(kardexDTO.getClienteProveedorId());
			kardex.setClienteProveedor(clienteProveedor);
		}

		kardex.setEstado(estado);
		kardex.setAlmacen(almacen);
		kardex.setProduccion(produccion);
		kardex.setTipoMovimiento(tipoMovimiento);
		kardex.setPedido(pedido);
		kardex.setOrdenCompra(ordenCompra);

		asignarDatosAuditoria(kardex);

	}

	private void asignarDatosAuditoria(Kardex kardex) {

		kardex.setIp(requestUtils.getClientIp(request));
		kardex.setHost(requestUtils.getClientHost(request));

		kardex.setUsername(authenticationService.getAuthenticatedUser().getUsername());

		kardex.setRol(requestUtils.getAuthenticatedRole());
	}

}
