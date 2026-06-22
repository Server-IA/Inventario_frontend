/*=============================================================================
 Nombre del archivo : KardexService.java
 Descripcion        : Servicio para la gestión y lógica de negocio del Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-21 | 0.4.0   | JUAN JOSE CASTRO     | Eliminación del seteo       |
 |            |         |                      | manual del atributo         |
 |            |         |                      | username a partir de los    |
 |            |         |                      | metadatos de seguridad en   |
 |            |         |                      | la creación y actualización |
 |            |         |                      | de los ítems del Kardex.    |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.kardex.services;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.custom.EntidadNoEncontradaException;
import com.coagronet.exceptionHandler.custom.MovimientoInvalidoException;
import com.coagronet.exceptionHandler.custom.ProductoSinResponsableException;
import com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException;
import com.coagronet.exceptionHandler.custom.ValidacionKardexException;
import com.coagronet.infrastructure.security.CustomUserDetails;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.ArticuloRequestDTO;
import com.coagronet.kardex.dtos.ArticuloUpdateRequestDTO;
import com.coagronet.kardex.dtos.ArticuloUpdateResponseDTO;
import com.coagronet.kardex.dtos.KardexAdminListDTO;
import com.coagronet.kardex.dtos.KardexListDTO;
import com.coagronet.kardex.dtos.KardexRequestDTO;
import com.coagronet.kardex.dtos.KardexUpdateRequestDTO;
import com.coagronet.kardex.dtos.KardexUpdateResponseDTO;
import com.coagronet.kardex.dtos.MetadatosSeguridad;
import com.coagronet.kardex.repositories.KardexAdminSpecifications;
import com.coagronet.kardex.repositories.KardexAdminViewRepository;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.kardex.repositories.KardexSpecifications;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.ordenCompra.repositories.OrdenCompraRepository;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedido.repositories.PedidoRepository;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.presentacionProducto.repositories.PresentacionProductoRepository;
import com.coagronet.produccion.Produccion;
import com.coagronet.produccion.repositories.ProduccionRepository;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoMovimiento.repositories.TipoMovimientoRepository;
import com.coagronet.user.User;
import com.coagronet.usuariorol.repositories.UsuarioRolRepository;
import com.coagronet.utils.UserEmpresaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KardexService {

	private final KardexRepository kardexRepository;

	private final TipoMovimientoRepository tipoMovimientoRepository;

	private final AlmacenRepository almacenRepository;

	private final OrdenCompraRepository ordenCompraRepository;

	private final PedidoRepository pedidoRepository;

	private final ProduccionRepository produccionRepository;

	private final EmpresaRepository empresaRepository;

	private final ArticuloKardexRepository articuloKardexRepository;

	private final PresentacionProductoRepository presentacionProductoRepository;

	private final UsuarioRolRepository usuarioRolRepository;

	private final EntityManager entityManager;

	private final UserEmpresaService userEmpresaService;

	private static final Long ESTADO_ACTIVO = 1L;

	private static final Long ESTADO_INACTIVO = 2L;

	private final KardexAdminViewRepository kardexAdminViewRepository;

	@Transactional
	public void inactivarMovimiento(Long kardexId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		int filasAfectadas = kardexRepository.inactivarKardex(kardexId, empresaId, ESTADO_ACTIVO, ESTADO_INACTIVO);

		if (filasAfectadas == 0) {
			throw new MovimientoInvalidoException(kardexId);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Kardex procesarMovimientoKardex(KardexRequestDTO request, MetadatosSeguridad metadata) {

		// 1. EXTRAER EL TENANT DEL CONTEXTO DE SEGURIDAD (Stateless)
		CustomUserDetails currentUser = (CustomUserDetails) SecurityContextHolder.getContext()
				.getAuthentication()
				.getPrincipal();
		Long currentEmpresaId = currentUser.empresaId();

		// 2. VALIDACIONES PREVIAS Y PRE-CARGA EN LOTE (Evita el N+1)
		Set<Long> idsPresentaciones = new HashSet<>();
		Set<Long> idsResponsables = new HashSet<>();

		for (ArticuloRequestDTO item : request.items()) {
			idsPresentaciones.add(item.presentacionProductoId());

			if (item.devolutivo() && item.responsableId() == null) {
				throw new ProductoSinResponsableException(String
						.format("El producto '%s' es devolutivo y requiere responsable.",
								item.presentacionProductoId()));
			}
			if (item.responsableId() != null) {
				idsResponsables.add(item.responsableId());
			}
		}

		// 3. VALIDACI?N BULK DE RESPONSABLES CONTRA LA EMPRESA ACTUAL
		if (!idsResponsables.isEmpty()) {
			Set<Long> responsablesValidos = usuarioRolRepository.findResponsablesValidos(idsResponsables,
					currentEmpresaId, ESTADO_ACTIVO);

			if (responsablesValidos.size() != idsResponsables.size()) {
				throw new IllegalStateException(
						"Uno o m?s responsables asignados no existen o no son empleados activos de tu empresa.");
			}
		}

		// 4. VALIDACI?N BULK DE PRESENTACIONES
		Map<Long, PresentacionProducto> mapaPresentaciones = presentacionProductoRepository
				.findAllByIdInAndEstado(idsPresentaciones, ESTADO_ACTIVO)
				.stream()
				.collect(Collectors.toMap(PresentacionProducto::getId, p -> p));

		if (mapaPresentaciones.size() != idsPresentaciones.size()) {
			Long idFaltante = idsPresentaciones.stream()
					.filter(id -> !mapaPresentaciones.containsKey(id))
					.findFirst()
					.orElse(null);

			throw new EntidadNoEncontradaException("PresentacionProducto", idFaltante);
		}

		// 5. HIDRATACI?N DEL KARDEX (Cabecera)
		TipoMovimiento tipoMovimiento = tipoMovimientoRepository
				.findByIdAndEstadoId(request.tipoMovimientoId(), ESTADO_ACTIVO)
				.orElseThrow(() -> new EntidadNoEncontradaException("TipoMovimiento", request.tipoMovimientoId()));

		Almacen almacen = almacenRepository.findByIdAndEstadoId(request.almacenId(), ESTADO_ACTIVO)
				.orElseThrow(() -> new EntidadNoEncontradaException("Almacen", request.almacenId()));

		Almacen almacenDestino = null;
		if (request.almacenDestinoId() != null) {
			if (request.almacenId().equals(request.almacenDestinoId())) {
				throw new ValidacionKardexException(
						"El almacén de origen y el almacén de destino no pueden ser el mismo.");
			}

			almacenDestino = almacenRepository.findByIdAndEstadoId(request.almacenDestinoId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("AlmacenDestino", request.almacenDestinoId()));
		}

		OrdenCompra ordenCompra = null;
		if (request.ordenCompraId() != null) {
			ordenCompra = ordenCompraRepository.findByIdAndEstadoId(request.ordenCompraId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("OrdenCompra", request.ordenCompraId()));
		}

		Pedido pedido = null;
		if (request.pedidoId() != null) {
			pedido = pedidoRepository.findByIdAndEstadoId(request.pedidoId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("Pedido", request.pedidoId()));
		}

		Produccion produccion = null;
		if (request.produccionId() != null) {
			produccion = produccionRepository.findByIdAndEstadoId(request.produccionId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("Produccion", request.produccionId()));
		}

		Empresa clienteProveedor = null;
		if (request.clienteProveedorId() != null) {
			clienteProveedor = empresaRepository.findByIdAndEstadoId(request.clienteProveedorId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("Empresa", request.clienteProveedorId()));
		}

		Kardex kardex = Kardex.builder()
				.tipoMovimiento(tipoMovimiento)
				.almacen(almacen)
				.almacenDestino(almacenDestino)
				.ordenCompra(ordenCompra)
				.pedido(pedido)
				.produccion(produccion)
				.clienteProveedor(clienteProveedor)
				.descripcion(request.descripcion())
				.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO))
				.username(metadata.username())
				.rol(metadata.rol())
				.ip(metadata.ip())
				.host(metadata.host())
				.build();

		Kardex kardexGuardado = kardexRepository.save(kardex);

		// 6. CREACI?N DE ART?CULOS
		List<ArticuloKardex> articulosAPersistir = new ArrayList<>();

		for (ArticuloRequestDTO itemDTO : request.items()) {
			PresentacionProducto presentacionValidada = mapaPresentaciones.get(itemDTO.presentacionProductoId());

			if (itemDTO.devolutivo()) {
				for (int i = 0; i < itemDTO.cantidad(); i++) {
					articulosAPersistir.add(
							construirArticulo(itemDTO, kardexGuardado, presentacionValidada, BigDecimal.ONE, metadata));
				}
			} else {
				articulosAPersistir.add(construirArticulo(itemDTO, kardexGuardado, presentacionValidada,
						BigDecimal.valueOf(itemDTO.cantidad()), metadata));
			}
		}

		articuloKardexRepository.saveAll(articulosAPersistir);

		return kardexGuardado;
	}

	private ArticuloKardex construirArticulo(ArticuloRequestDTO dto, Kardex kardex, PresentacionProducto presentacion,
			BigDecimal cantidad, MetadatosSeguridad metadata) {

		return ArticuloKardex.builder()
				.kardex(kardex)
				.presentacionProducto(presentacion)
				.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO))
				.responsable(
						dto.responsableId() != null ? entityManager.getReference(User.class, dto.responsableId())
								: null)
				.cantidad(cantidad)
				.precio(dto.precio())
				.lote(dto.lote())
				.fechaVencimiento(dto.fechaVencimiento())
				.rol(metadata.rol())
				.ip(metadata.ip())
				.host(metadata.host())
				.build();
	}

	@Transactional(readOnly = true)
	public Page<?> listarMovimientos(OffsetDateTime fechaInicio, OffsetDateTime fechaFin,
			Long tipoMovimientoId, Long estadoId, Pageable pageable) {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		boolean isAdmin = auth != null && auth.getAuthorities()
				.stream()
				.map(GrantedAuthority::getAuthority)
				.anyMatch(role -> role.equals("ROLE_ADMINISTRADOR_SISTEMA"));

		if (isAdmin) {
			var specAdmin = KardexAdminSpecifications.conFiltros(fechaInicio, fechaFin, tipoMovimientoId, estadoId);

			return kardexAdminViewRepository.findAll(specAdmin, pageable)
					.map(view -> new KardexAdminListDTO(
							view.getId(),
							view.getFechaHora(),
							view.getNombreAlmacen(),
							view.getNombreTipoMovimiento(),
							view.getNombreProduccion(),
							view.getNombreEstado(),
							view.getNombreEmpresa(),
							view.getNombreClienteProveedor(),
							view.getNombreAlmacenDestino()));
		}

		var spec = KardexSpecifications.conFiltros(fechaInicio, fechaFin, tipoMovimientoId, estadoId);

		return kardexRepository.findAll(spec, pageable)
				.map(kardex -> new KardexListDTO(
						kardex.getId(),
						kardex.getFechaHora(),
						kardex.getAlmacen().getNombre(),
						kardex.getTipoMovimiento().getNombre(),
						kardex.getProduccion() != null ? kardex.getProduccion().getNombre() : null,
						kardex.getEstado().getNombre(),
						kardex.getClienteProveedor() != null ? kardex.getClienteProveedor().getNombre() : null,
						kardex.getAlmacenDestino() != null ? kardex.getAlmacenDestino().getNombre() : null));
	}

	@Transactional(readOnly = true)
	public KardexUpdateResponseDTO getKardexForUpdate(Long id) {
		return kardexRepository.findWithItemsById(id)
				.map(this::mapToUpdateDTO)
				.orElseThrow(() -> new EntityNotFoundException("Kardex no encontrado con ID: " + id));
	}

	private KardexUpdateResponseDTO mapToUpdateDTO(Kardex kardex) {
		return new KardexUpdateResponseDTO(kardex.getId(),
				kardex.getTipoMovimiento() != null ? kardex.getTipoMovimiento().getId() : null,
				kardex.getAlmacen() != null ? kardex.getAlmacen().getId() : null,
				kardex.getAlmacenDestino() != null ? kardex.getAlmacenDestino().getId() : null,
				kardex.getOrdenCompra() != null ? kardex.getOrdenCompra().getId() : null,
				kardex.getPedido() != null ? kardex.getPedido().getId() : null,
				kardex.getProduccion() != null ? kardex.getProduccion().getId() : null,
				kardex.getClienteProveedor() != null ? kardex.getClienteProveedor().getId() : null,
				kardex.getDescripcion(), kardex.getItems().stream().map(this::mapItemToUpdateDTO).toList());
	}

	private ArticuloUpdateResponseDTO mapItemToUpdateDTO(ArticuloKardex item) {
		return new ArticuloUpdateResponseDTO(item.getId(),
				item.getPresentacionProducto() != null ? item.getPresentacionProducto().getId() : null,
				item.getCantidad(), item.getPrecio(),
				item.getResponsable() != null ? item.getResponsable().getId() : null, item.getLote(),
				item.getFechaVencimiento());
	}

	@Transactional(rollbackFor = Exception.class)
	public Kardex actualizarMovimientoKardex(Long kardexId, KardexUpdateRequestDTO request,
			MetadatosSeguridad metadata) {

		// 1. Carga de cabecera
		Kardex kardex = kardexRepository.findById(kardexId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Kardex", kardexId));

		// 2. Validaci?n de integridad empresarial (multitenancy)
		validarRelacionesEmpresa(kardex, request);

		// 3. Actualizaci?n de campos maestros
		actualizarCamposMaestros(kardex, request, metadata);

		// 4. Sincronizaci?n de ?tems con l?gica de devolutivos y sin eliminaci?n f?sica
		sincronizarItems(kardex, request.items(), metadata);

		return kardex;
	}

	private void validarRelacionesEmpresa(Kardex kardex, KardexUpdateRequestDTO request) {
		almacenRepository.findByIdAndEstadoId(request.almacenId(), ESTADO_ACTIVO)
				.orElseThrow(() -> new EntidadNoEncontradaException("Almacen", request.almacenId()));

		if (request.almacenDestinoId() != null) {
			if (request.almacenId().equals(request.almacenDestinoId())) {
				throw new ValidacionKardexException(
						"El almacén de origen y el almacén de destino no pueden ser el mismo.");
			}

			almacenRepository.findByIdAndEstadoId(request.almacenDestinoId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("Almacen Destino", request.almacenDestinoId()));
		}

		if (request.ordenCompraId() != null)

		{
			ordenCompraRepository.findByIdAndEstadoId(request.ordenCompraId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("OrdenCompra", request.ordenCompraId()));

		}

		if (request.pedidoId() != null) {
			pedidoRepository.findByIdAndEstadoId(request.pedidoId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("Pedido", request.pedidoId()));

		}

		if (request.produccionId() != null) {
			produccionRepository.findByIdAndEstadoId(request.produccionId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("Produccion", request.produccionId()));
		}

		if (request.clienteProveedorId() != null) {
			empresaRepository.findByIdAndEstadoId(request.clienteProveedorId(), ESTADO_ACTIVO)
					.orElseThrow(
							() -> new EntidadNoEncontradaException("ClienteProveedorId", request.clienteProveedorId()));
		}
	}

	private void actualizarCamposMaestros(Kardex kardex, KardexUpdateRequestDTO request, MetadatosSeguridad metadata) {
		// Asignaci?n de almacenes, OC, pedido, producci?n, cliente/proveedor
		// y metadatos de auditor?a.
		kardex.setAlmacen(entityManager.getReference(Almacen.class, request.almacenId()));
		kardex.setAlmacenDestino(request.almacenDestinoId() != null
				? entityManager.getReference(Almacen.class, request.almacenDestinoId())
				: null);
		kardex.setOrdenCompra(request.ordenCompraId() != null
				? entityManager.getReference(OrdenCompra.class, request.ordenCompraId())
				: null);
		kardex.setPedido(
				request.pedidoId() != null ? entityManager.getReference(Pedido.class, request.pedidoId()) : null);
		kardex.setProduccion(request.produccionId() != null
				? entityManager.getReference(Produccion.class, request.produccionId())
				: null);
		kardex.setClienteProveedor(request.clienteProveedorId() != null
				? entityManager.getReference(Empresa.class, request.clienteProveedorId())
				: null);
		kardex.setDescripcion(request.descripcion());
	}

	private void sincronizarItems(Kardex kardex, List<ArticuloUpdateRequestDTO> itemsRequest,
			MetadatosSeguridad metadata) {

		Map<Long, ArticuloKardex> itemsExistentes = kardex.getItems()
				.stream()
				.filter(item -> item.getId() != null)
				.collect(Collectors.toMap(ArticuloKardex::getId, Function.identity()));

		Set<Long> idsProcesados = new HashSet<>();

		for (ArticuloUpdateRequestDTO itemDTO : itemsRequest) {
			PresentacionProducto presentacion = presentacionProductoRepository
					.findByIdInAndEstadoId(itemDTO.presentacionProductoId(), ESTADO_ACTIVO)
					.orElseThrow(() -> new EntidadNoEncontradaException("PresentacionProducto",
							itemDTO.presentacionProductoId()));
			if (itemDTO.id() != null && itemsExistentes.containsKey(itemDTO.id())) {
				// Actualizaci?n de ?tem existente
				ArticuloKardex existente = itemsExistentes.get(itemDTO.id());
				procesarActualizacionItem(existente, itemDTO, presentacion, metadata, kardex);
				idsProcesados.add(itemDTO.id());
			} else if (itemDTO.id() == null) {
				// Nuevo ?tem
				List<ArticuloKardex> nuevos = crearItemsDesdeDTO(itemDTO, presentacion, kardex, metadata);
				kardex.getItems().addAll(nuevos);
			}
		}

		// Inactivaci?n l?gica de ?tems no incluidos (NO ELIMINACI?N F?SICA)
		for (ArticuloKardex item : kardex.getItems()) {
			if (item.getId() != null && !idsProcesados.contains(item.getId())) {
				item.setEstado(entityManager.getReference(Estado.class, ESTADO_INACTIVO));
				actualizarAuditoriaItem(item, metadata);
			}
		}
	}

	private void procesarActualizacionItem(ArticuloKardex existente, ArticuloUpdateRequestDTO dto,
			PresentacionProducto presentacion, MetadatosSeguridad metadata, Kardex kardex) {

		boolean esDevolutivo = presentacion.getDesgregar();

		if (esDevolutivo) {
			// Para productos devolutivos, no se permite actualizar cantidad directamente
			// Se debe inactivar el ?tem actual y generar los nuevos seg?n la cantidad
			// solicitada
			if (!Objects.equals(existente.getCantidad(), dto.cantidad())) {
				// Inactivar ?tem antiguo
				existente.setEstado(entityManager.getReference(Estado.class, ESTADO_INACTIVO));
				actualizarAuditoriaItem(existente, metadata);

				// Crear nuevos ?tems individuales (uno por unidad)
				List<ArticuloKardex> nuevos = crearItemsDesdeDTO(dto, presentacion, kardex, metadata);
				kardex.getItems().addAll(nuevos);
				return;
			}
			// Si cantidad no cambi?, se actualizan otros campos
		}

		// Actualizaci?n normal para productos no devolutivos
		existente.setCantidad(dto.cantidad());
		existente.setPrecio(dto.precio());
		existente.setPresentacionProducto(presentacion);
		existente.setLote(dto.lote());
		existente.setFechaVencimiento(dto.fechaVencimiento());
		existente.setResponsable(obtenerReferenciaUsuario(dto.responsableId()));
		actualizarAuditoriaItem(existente, metadata);
	}

	private List<ArticuloKardex> crearItemsDesdeDTO(ArticuloUpdateRequestDTO dto, PresentacionProducto presentacion,
			Kardex kardex, MetadatosSeguridad metadata) {

		boolean esDevolutivo = presentacion.getDesgregar();
		List<ArticuloKardex> items = new ArrayList<>();

		if (esDevolutivo) {
			// Validaci?n: responsable obligatorio
			if (dto.responsableId() == null) {
				throw new ProductoSinResponsableException("Producto devolutivo requiere responsable asignado");
			}
			// Crear un ?tem por cada unidad
			for (int i = 0; i < dto.cantidad().intValueExact(); i++) {
				ArticuloKardex item = ArticuloKardex.builder()
						.kardex(kardex)
						.presentacionProducto(presentacion)
						.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO))
						.responsable(obtenerReferenciaUsuario(dto.responsableId()))
						.cantidad(BigDecimal.ONE)
						.precio(dto.precio())
						.lote(dto.lote())
						.fechaVencimiento(dto.fechaVencimiento())
						.rol(metadata.rol())
						.ip(metadata.ip())
						.host(metadata.host())
						.build();
				items.add(item);
			}
		} else {
			// Producto normal: un solo ?tem con la cantidad indicada
			ArticuloKardex item = ArticuloKardex.builder()
					.kardex(kardex)
					.presentacionProducto(presentacion)
					.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO))
					.responsable(obtenerReferenciaUsuario(dto.responsableId()))
					.cantidad(dto.cantidad())
					.precio(dto.precio())
					.lote(dto.lote())
					.fechaVencimiento(dto.fechaVencimiento())
					.rol(metadata.rol())
					.ip(metadata.ip())
					.host(metadata.host())
					.build();
			items.add(item);
		}
		return items;
	}

	private void actualizarAuditoriaItem(ArticuloKardex item, MetadatosSeguridad metadata) {
		item.setRol(metadata.rol());
		item.setIp(metadata.ip());
		item.setHost(metadata.host());
	}

	private User obtenerReferenciaUsuario(Long usuarioId) {
		if (usuarioId == null)
			return null;
		return entityManager.getReference(User.class, usuarioId);
	}

}
