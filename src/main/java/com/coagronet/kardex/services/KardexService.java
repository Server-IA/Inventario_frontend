package com.coagronet.kardex.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.almacen.Almacen;
import com.coagronet.almacen.repositories.AlmacenRepository;
import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.auditoria.RequestUtils;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresa.repositories.EmpresaRepository;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.custom.EntidadNoEncontradaException;
import com.coagronet.exceptionHandler.custom.MovimientoInvalidoException;
import com.coagronet.exceptionHandler.custom.ProductoSinResponsableException;
import com.coagronet.infrastructure.security.CustomUserDetails;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.ArticuloKardexListDto;
import com.coagronet.kardex.dtos.ArticuloRequestDTO;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.dtos.KardexRequestDTO;
import com.coagronet.kardex.dtos.MetadatosSeguridad;
import com.coagronet.kardex.dtos.MovimientoKardexDTO;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
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
import com.coagronet.validator.EntidadValidatorFacade;

import jakarta.persistence.EntityManager;
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

	private final KardexMapper kardexMapper;

	private final UserEmpresaService userEmpresaService;

	private final EntidadValidatorFacade entidadValidatorFacade;

	private final AuthenticationService authenticationService;

	private final RequestUtils requestUtils;

	private static final Long ESTADO_ACTIVO = 1L;

	private static final Long ESTADO_INACTIVO = 2L;

	@Transactional(readOnly = true)
	public Page<KardexDTO> findAll(Pageable pageable) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return kardexRepository.findDtoByEmpresaIdOrderByIdAsc(empresaId, pageable);
	}

	@Transactional(readOnly = true)
	public Optional<KardexDTO> findById(Long requestedId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		return kardexRepository.findDtoByIdAndEmpresaId(requestedId, empresaId);
	}

	@Transactional
	public KardexDTO create(KardexDTO kardexDTO, String clientIp, String clientHost) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();
		kardexDTO.setEmpresaId(empresaId);

		Kardex kardex = kardexMapper.toEntity(kardexDTO);
		aplicarValidacionesYRelaciones(kardexDTO, kardex, empresaId);
		asignarDatosAuditoria(kardex, clientIp, clientHost);

		Kardex guardado = kardexRepository.save(kardex);
		return kardexMapper.toDto(guardado);
	}

	@Transactional
	public void update(Long requestedId, KardexDTO kardexDTO, String clientIp, String clientHost) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		// Esta entidad ya está dentro del Unit of Work
		Kardex kardexExistente = entidadValidatorFacade.obtenerParaMutacion(requestedId, empresaId);

		kardexMapper.updateEntityFromDto(kardexDTO, kardexExistente);
		aplicarValidacionesYRelaciones(kardexDTO, kardexExistente, empresaId);
		asignarDatosAuditoria(kardexExistente, clientIp, clientHost);

	}

	@Transactional
	public void inactivarMovimiento(Long kardexId) {
		Long empresaId = userEmpresaService.getEmpresaIdFromCurrentRequest();

		int filasAfectadas = kardexRepository.inactivarKardex(kardexId, empresaId, ESTADO_ACTIVO, ESTADO_INACTIVO);

		if (filasAfectadas == 0) {
			throw new MovimientoInvalidoException(kardexId);
		}
	}

	// Nota Arquitectónica: Si EntidadValidatorFacade.validarAlmacen() solo retorna la
	// entidad
	// sin hacer validaciones complejas de BD, cámbialo a EntityManager.getReference()
	// para usar Proxies y evitar ejecutar consultas SELECT masivas (N+1).
	private void aplicarValidacionesYRelaciones(KardexDTO kardexDTO, Kardex kardex, Long empresaId) {
		kardex.setEstado(entidadValidatorFacade.validarEstadoGeneral(kardexDTO.getEstadoId()));
		kardex.setAlmacen(entidadValidatorFacade.validarAlmacen(kardexDTO.getAlmacenId(), empresaId));
		kardex.setProduccion(entidadValidatorFacade.validarProduccion(kardexDTO.getProduccionId(), empresaId));
		kardex.setTipoMovimiento(
				entidadValidatorFacade.validarTipoMovimiento(kardexDTO.getTipoMovimientoId(), empresaId));

		kardex.setPedido(kardexDTO.getPedidoId() != null
				? entidadValidatorFacade.validarPedido(kardexDTO.getPedidoId(), empresaId) : null);

		kardex.setOrdenCompra(kardexDTO.getOrdenCompraId() != null
				? entidadValidatorFacade.validarOrdenCompra(kardexDTO.getOrdenCompraId(), empresaId) : null);

		if (kardexDTO.getClienteProveedorId() != null) {
			kardex
				.setClienteProveedor(entidadValidatorFacade.validarClienteProveedor(kardexDTO.getClienteProveedorId()));
		}
	}

	private void asignarDatosAuditoria(Kardex kardex, String ip, String host) {
		kardex.setIp(ip);
		kardex.setHost(host);
		kardex.setUsername(authenticationService.getAuthenticatedUser().getUsername());
		kardex.setRol(requestUtils.getAuthenticatedRole());
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
					.format("El producto '%s' es devolutivo y requiere responsable.", item.presentacionProductoId()));
			}
			if (item.responsableId() != null) {
				idsResponsables.add(item.responsableId());
			}
		}

		// 3. VALIDACIÓN BULK DE RESPONSABLES CONTRA LA EMPRESA ACTUAL
		if (!idsResponsables.isEmpty()) {
			Set<Long> responsablesValidos = usuarioRolRepository.findResponsablesValidos(idsResponsables,
					currentEmpresaId, ESTADO_ACTIVO);

			if (responsablesValidos.size() != idsResponsables.size()) {
				throw new IllegalStateException(
						"Uno o más responsables asignados no existen o no son empleados activos de tu empresa.");
			}
		}

		// 4. VALIDACIÓN BULK DE PRESENTACIONES
		Map<Long, PresentacionProducto> mapaPresentaciones = presentacionProductoRepository
			.findByIdInAndEstadoId(idsPresentaciones, ESTADO_ACTIVO)
			.stream()
			.collect(Collectors.toMap(PresentacionProducto::getId, p -> p));

		if (mapaPresentaciones.size() != idsPresentaciones.size()) {
			Long idFaltante = idsPresentaciones.stream()
				.filter(id -> !mapaPresentaciones.containsKey(id))
				.findFirst()
				.orElse(null);

			throw new EntidadNoEncontradaException("PresentacionProducto", idFaltante);
		}

		// 5. HIDRATACIÓN DEL KARDEX (Cabecera)
		TipoMovimiento tipoMovimiento = tipoMovimientoRepository
			.findByIdAndEstadoId(request.tipoMovimientoId(), ESTADO_ACTIVO)
			.orElseThrow(() -> new EntidadNoEncontradaException("TipoMovimiento", request.tipoMovimientoId()));

		Almacen almacen = almacenRepository.findByIdAndEstadoId(request.almacenId(), ESTADO_ACTIVO)
			.orElseThrow(() -> new EntidadNoEncontradaException("Almacen", request.almacenId()));

		Almacen almacenDestino = null;
		if (request.almacenDestinoId() != null) {
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

		// 6. CREACIÓN DE ARTÍCULOS
		List<ArticuloKardex> articulosAPersistir = new ArrayList<>();

		for (ArticuloRequestDTO itemDTO : request.items()) {
			PresentacionProducto presentacionValidada = mapaPresentaciones.get(itemDTO.presentacionProductoId());

			if (itemDTO.devolutivo()) {
				for (int i = 0; i < itemDTO.cantidad(); i++) {
					articulosAPersistir.add(
							construirArticulo(itemDTO, kardexGuardado, presentacionValidada, BigDecimal.ONE, metadata));
				}
			}
			else {
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
					dto.responsableId() != null ? entityManager.getReference(User.class, dto.responsableId()) : null)
			.cantidad(cantidad)
			.precio(dto.precio())
			.lote(dto.lote())
			.fechaVencimiento(dto.fechaVencimiento())
			.username(metadata.username())
			.rol(metadata.rol())
			.ip(metadata.ip())
			.host(metadata.host())
			.build();
	}

	public Page<MovimientoKardexDTO> listarMovimientos(Pageable pageable) {
		return articuloKardexRepository.findMovimientosPaginados(pageable);
	}

	@Transactional(readOnly = true)
	public Page<ArticuloKardexListDto> obtenerMovimientosPorAlmacen(Long almacenId, Pageable pageable) {
		return articuloKardexRepository.findMovimientosByAlmacenId(almacenId, pageable);
	}

}
