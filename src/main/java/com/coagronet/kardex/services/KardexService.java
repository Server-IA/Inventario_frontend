package com.coagronet.kardex.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.almacen.Almacen;
import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.articuloKardex.repositories.ArticuloKardexRepository;
import com.coagronet.auditoria.AuthenticationService;
import com.coagronet.auditoria.RequestUtils;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.exceptionHandler.custom.MovimientoInvalidoException;
import com.coagronet.exceptionHandler.custom.ProductoSinResponsableException;
import com.coagronet.kardex.Kardex;
import com.coagronet.kardex.dtos.ArticuloRequestDTO;
import com.coagronet.kardex.dtos.KardexDTO;
import com.coagronet.kardex.dtos.KardexRequestDTO;
import com.coagronet.kardex.dtos.MetadatosSeguridad;
import com.coagronet.kardex.mappers.KardexMapper;
import com.coagronet.kardex.repositories.KardexRepository;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.user.User;
import com.coagronet.utils.UserEmpresaService;
import com.coagronet.validator.EntidadValidatorFacade;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KardexService {

	private final KardexRepository kardexRepository;

	private final ArticuloKardexRepository articuloKardexRepository;

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
		// Sugerencia: El AuthenticationService también pertenece a un contexto Web
		// Security.
		// Si es posible, extrae esto en la capa del controlador.
		kardex.setUsername(authenticationService.getAuthenticatedUser().getUsername());
		kardex.setRol(requestUtils.getAuthenticatedRole());
	}

	@Transactional(rollbackFor = Exception.class)
	public Kardex procesarMovimientoKardex(KardexRequestDTO request, MetadatosSeguridad metadata, Long empresaId) {

		request.getItems().forEach(itemDTO -> {
			if (itemDTO.isDevolutivo() && itemDTO.getResponsableId() == null) {
				throw new ProductoSinResponsableException(String.format(
						"El producto de presentación '%s' es devolutivo y requiere un responsable asignado.",
						itemDTO.getPresentacionProductoId()));
			}
		});

		Empresa empresaProxy = entityManager.getReference(Empresa.class, empresaId);

		Kardex kardex = Kardex.builder()
			.empresa(empresaProxy)
			.tipoMovimiento(entityManager.getReference(TipoMovimiento.class, request.getTipoMovimientoId()))
			.almacen(entityManager.getReference(Almacen.class, request.getAlmacenId()))
			.ordenCompra(request.getOrdenCompraId() != null
					? entityManager.getReference(OrdenCompra.class, request.getOrdenCompraId()) : null)
			.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO))
			.username(metadata.username())
			.rol(metadata.rol())
			.ip(metadata.ip())
			.host(metadata.host())
			.build();

		Kardex kardexGuardado = kardexRepository.save(kardex);

		List<ArticuloKardex> articulosAPersistir = new ArrayList<>(request.getItems().size());

		for (ArticuloRequestDTO itemDTO : request.getItems()) {
			if (itemDTO.isDevolutivo()) {

				for (int i = 0; i < itemDTO.getCantidad(); i++) {
					articulosAPersistir
						.add(construirArticulo(itemDTO, kardexGuardado, BigDecimal.ONE, metadata, empresaProxy));
				}
			}
			else {

				articulosAPersistir.add(construirArticulo(itemDTO, kardexGuardado,
						BigDecimal.valueOf(itemDTO.getCantidad()), metadata, empresaProxy));
			}
		}

		articuloKardexRepository.saveAll(articulosAPersistir);

		return kardexGuardado;
	}

	private ArticuloKardex construirArticulo(ArticuloRequestDTO dto, Kardex kardex, BigDecimal cantidad,
			MetadatosSeguridad metadata, Empresa empresaProxy) {

		return ArticuloKardex.builder()
			.kardex(kardex)
			.presentacionProducto(
					entityManager.getReference(PresentacionProducto.class, dto.getPresentacionProductoId()))
			.empresa(empresaProxy)
			.estado(entityManager.getReference(Estado.class, ESTADO_ACTIVO))
			.responsable(dto.getResponsableId() != null ? entityManager.getReference(User.class, dto.getResponsableId())
					: null)
			.cantidad(cantidad)
			.precio(dto.getPrecio())
			.username(metadata.username())
			.rol(metadata.rol())
			.ip(metadata.ip())
			.host(metadata.host())
			.build();
	}

}
