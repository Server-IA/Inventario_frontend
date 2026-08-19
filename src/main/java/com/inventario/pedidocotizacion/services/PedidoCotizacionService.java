package com.inventario.pedidocotizacion.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.estado.Estado;
import com.inventario.exceptionHandler.NotFoundException;
import com.inventario.pedido.Pedido;
import com.inventario.pedidocotizacion.PedidoCotizacion;
import com.inventario.pedidocotizacion.dtos.PedidoCotizacionRequestDTO;
import com.inventario.pedidocotizacion.dtos.PedidoCotizacionResponseDTO;
import com.inventario.pedidocotizacion.mappers.PedidoCotizacionMapper;
import com.inventario.pedidocotizacion.repositories.PedidoCotizacionRepository;
import com.inventario.proveedor.Proveedor;
import com.inventario.utils.UserEmpresaService;
import com.inventario.validator.EntidadValidatorFacade;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Servicio encargado de gestionar las operaciones del módulo de
 * {@link PedidoCotizacion}.
 * 
 * <p>
 * Implementa la lógica de negocio para la creación, actualización, eliminación
 * y consulta de cotizaciones asociadas a pedidos, garantizando la integridad
 * de los datos y el cumplimiento de las reglas de negocio relacionadas con
 * la empresa autenticada.
 * </p>
 *
 * <p>
 * Este servicio respeta los principios SOLID al delegar la validación de
 * entidades
 * al componente {@link EntidadValidatorFacade}, la conversión de objetos al
 * {@link PedidoCotizacionMapper}, y la obtención del contexto de empresa
 * al {@link UserEmpresaService}.
 * </p>
 *
 * @author Juan J.
 * @version 0.3.1
 * @since 0.3.1
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class PedidoCotizacionService {

	/**
	 * Repositorio JPA para el manejo de persistencia de {@link PedidoCotizacion}.
	 */
	private final PedidoCotizacionRepository pedidoCotizacionRepository;

	/** Mapper encargado de convertir entre entidades y DTOs de PedidoCotización. */
	private final PedidoCotizacionMapper pedidoCotizacionMapper;

	/** Servicio para obtener el ID de la empresa actual a partir del token JWT. */
	private final UserEmpresaService userEmpresaService;

	/** Fachada que centraliza la validación de entidades relacionadas. */
	private final EntidadValidatorFacade entidadValidatorFacade;

	/**
	 * Obtiene una lista paginada de todas las cotizaciones de pedidos
	 * asociadas a la empresa autenticada.
	 *
	 * @param pageable configuración de paginación y ordenamiento
	 * @return página de {@link PedidoCotizacionResponseDTO} ordenada por ID
	 *         ascendente
	 */
	public Page<PedidoCotizacionResponseDTO> findAll(Pageable pageable) {
		return pedidoCotizacionRepository
				.findByPedidoEmpresaIdOrderByIdAsc(userEmpresaService.getEmpresaIdFromCurrentRequest(), pageable)
				.map(pedidoCotizacionMapper::toDTO);
	}

	/**
	 * Obtiene todas las cotizaciones asociadas a un pedido específico.
	 *
	 * @param pedidoId identificador del pedido
	 * @return lista de cotizaciones convertidas a
	 *         {@link PedidoCotizacionResponseDTO}
	 */
	public List<PedidoCotizacionResponseDTO> findAllByPedidoId(Long pedidoId) {
		return pedidoCotizacionRepository
				.findByPedidoIdAndPedidoEmpresaId(pedidoId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.stream()
				.map(pedidoCotizacionMapper::toDTO)
				.toList();
	}

	/**
	 * Busca una cotización de pedido por su identificador, validando que
	 * pertenezca a la empresa del usuario autenticado.
	 *
	 * @param requestedId identificador de la cotización a consultar
	 * @return {@link PedidoCotizacionResponseDTO} correspondiente
	 * @throws NotFoundException si la cotización no existe o no pertenece a la
	 *                           empresa
	 */
	public PedidoCotizacionResponseDTO findById(Long requestedId) {
		return pedidoCotizacionRepository
				.findByIdAndPedidoEmpresaId(requestedId, userEmpresaService.getEmpresaIdFromCurrentRequest())
				.map(pedidoCotizacionMapper::toDTO)
				.orElseThrow(() -> new NotFoundException("pedido-cotizacion.not-found", requestedId));
	}

	/**
	 * Crea una nueva cotización de pedido, validando previamente la existencia
	 * del pedido, proveedor y estado asociados.
	 *
	 * <p>
	 * Este método se ejecuta en una transacción para garantizar la integridad
	 * de la operación.
	 * </p>
	 *
	 * @param pedidoCotizacionRequestDTO datos de la cotización a registrar
	 * @return {@link PedidoCotizacionResponseDTO} correspondiente al registro
	 *         creado
	 */
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

	/**
	 * Actualiza una cotización existente, validando que pertenezca a la empresa
	 * actual
	 * y que las entidades relacionadas sean válidas.
	 *
	 * @param requestedId                identificador de la cotización a actualizar
	 * @param pedidoCotizacionRequestDTO datos actualizados de la cotización
	 * @throws NotFoundException si alguna de las entidades relacionadas no existe
	 */
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

	/**
	 * Elimina una cotización de pedido por su ID, previa validación de que
	 * pertenezca a la empresa autenticada.
	 *
	 * <p>
	 * La eliminación es lógica o física según la configuración del repositorio.
	 * </p>
	 *
	 * @param id identificador de la cotización a eliminar
	 * @throws NotFoundException si la cotización no existe o no pertenece a la
	 *                           empresa
	 */
	@Transactional
	public void delete(Long id) {
		entidadValidatorFacade.validarPedidoCotizacion(id, userEmpresaService.getEmpresaIdFromCurrentRequest());
		pedidoCotizacionRepository.deleteById(id);
	}
}
