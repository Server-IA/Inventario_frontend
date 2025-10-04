package com.coagronet.kardex.services;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.validator.EntidadValidatorFacade;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.NotFoundException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.pedido.Pedido;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.produccion.Produccion;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.utils.UserEmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KardexService {

	private final KardexRepository kardexRepository;

	private final KardexMapper kardexMapper;

	private final UserEmpresaService userEmpresaService;

	private final EntidadValidatorFacade entidadValidatorFacade;

	public Page<KardexDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return kardexRepository.findByEmpresaIdOrderByIdAsc(empresaId, pageable).map(kardexMapper::toDto);
	}

	public Optional<KardexDTO> findById(Long requestedId) {
		return kardexRepository.findByIdAndEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.map(kardexMapper::toDto);
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

		Kardex kardexExistente = entidadValidatorFacade.validarKardex(requestedId, empresaId);
		kardexMapper.updateEntityFromDto(kardexDTO, kardexExistente);

		aplicarValidacionesYRelaciones(kardexDTO, kardexExistente, empresaId);

		Kardex guardado = kardexRepository.save(kardexExistente);
		return kardexMapper.toDto(guardado);
	}

	@Transactional
	public void delete(Long requestId) {
		kardexRepository.findByIdAndEmpresaId(requestId, userEmpresaService.getEmpresaIdFromCurrentRequest())
			.orElseThrow(() -> new NotFoundException("Kardex no encontrado o no v�lido"));

		kardexRepository.deleteById(requestId);
	}

	private void aplicarValidacionesYRelaciones(KardexDTO kardexDTO, Kardex kardex, Long empresaId) {
		Estado estado = entidadValidatorFacade.validarEstadoGeneral(kardexDTO.getEstadoId());
		Almacen almacen = entidadValidatorFacade.validarAlmacen(kardexDTO.getAlmacenId(), empresaId);
		Produccion produccion = entidadValidatorFacade.validarProduccion(kardexDTO.getProduccionId(), empresaId);
		TipoMovimiento tipoMovimiento = entidadValidatorFacade.validarTipoMovimiento(kardexDTO.getTipoMovimientoId(),
				empresaId);
		Pedido pedido = entidadValidatorFacade.validarPedido(kardexDTO.getPedidoId(), empresaId);
		OrdenCompra ordenCompra = entidadValidatorFacade.validarOrdenCompra(kardexDTO.getOrdenCompraId(), empresaId);

		kardex.setEstado(estado);
		kardex.setAlmacen(almacen);
		kardex.setProduccion(produccion);
		kardex.setTipoMovimiento(tipoMovimiento);
		kardex.setPedido(pedido);
		kardex.setOrdenCompra(ordenCompra);

		if (kardexDTO.getClienteProveedorId() != null) {
			Empresa clienteProveedor = entidadValidatorFacade
				.validarClienteProveedor(kardexDTO.getClienteProveedorId());
			kardex.setClienteProveedor(clienteProveedor);
		}
	}

}
