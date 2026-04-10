package com.coagronet.validator;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.coagronet.almacen.Almacen;
import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.cierreinventario.CierreInventario;
import com.coagronet.cierreinventariodetalle.CierreInventarioDetalle;
import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.empresa.Empresa;
import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.espacio.Espacio;
import com.coagronet.estado.Estado;
import com.coagronet.evaluacion.Evaluacion;
import com.coagronet.evaluacionitem.EvaluacionItem;
import com.coagronet.movimiento.Movimiento;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedidocotizacion.PedidoCotizacion;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.produccion.Produccion;
import com.coagronet.productoCategoria.ProductoCategoria;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.rol.Rol;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.tipounidad.TipoUnidad;
import com.coagronet.validator.common.ValidatorRegistry;
import com.coagronet.validator.inventario.entidades.AlmacenValidator;
import com.coagronet.validator.inventario.entidades.ArticuloKardexValidator;
import com.coagronet.validator.inventario.entidades.CierreInventarioDetalleValidator;
import com.coagronet.validator.inventario.entidades.CierreInventarioValidator;
import com.coagronet.validator.inventario.entidades.CriterioEvaluacionValidator;
import com.coagronet.validator.inventario.entidades.EmpresaValidator;
import com.coagronet.validator.inventario.entidades.EspacioValidator;
import com.coagronet.validator.inventario.entidades.EvaluacionItemValidator;
import com.coagronet.validator.inventario.entidades.EvaluacionValidator;
import com.coagronet.validator.inventario.entidades.MovimientoValidator;
import com.coagronet.validator.inventario.entidades.OrdenCompraValidator;
import com.coagronet.validator.inventario.entidades.PedidoCotizacionValidator;
import com.coagronet.validator.inventario.entidades.PedidoValidator;
import com.coagronet.validator.inventario.entidades.ProduccionValidator;
import com.coagronet.validator.inventario.entidades.ProductoCategoriaValidator;
import com.coagronet.validator.inventario.entidades.ProductoPresentacionValidator;
import com.coagronet.validator.inventario.entidades.ProveedorValidator;
import com.coagronet.validator.parametrizacion.entidades.EstadoValidator;
import com.coagronet.validator.parametrizacion.entidades.SubseccionValidator;
import com.coagronet.validator.parametrizacion.entidades.TipoMovimientoValidator;
import com.coagronet.validator.parametrizacion.entidades.TipoProduccionValidator;
import com.coagronet.validator.parametrizacion.entidades.TipoUnidadValidator;
import com.coagronet.validator.seguridad.EmpresaRolValidator;
import com.coagronet.validator.seguridad.RolValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntidadValidatorFacade {

	private final ValidatorRegistry validatorRegistry;

	private final TipoMovimientoValidator tipoMovimientoValidator;

	private final PedidoValidator pedidoValidator;

	private final ProveedorValidator proveedorValidator;

	private final TipoProduccionValidator tipoProduccionValidator;

	private final SubseccionValidator subseccionValidator;

	private final ProductoPresentacionValidator productoPresentacionValidator;

	private final MovimientoValidator movimientoValidator;

	private final PedidoCotizacionValidator pedidoCotizacionValidator;

	private final EvaluacionValidator evaluacionValidator;

	private final CriterioEvaluacionValidator criterioEvaluacionValidator;

	private final EvaluacionItemValidator evaluacionItemValidator;

	private final ProductoCategoriaValidator productoCategoriaValidator;

	private final TipoUnidadValidator tipoUnidadValidator;

	public Empresa validarEmpresa(Long empresaId) {
		return validatorRegistry.getValidator(EmpresaValidator.class).validarEmpresa(empresaId);
	}

	public Empresa validarClienteProveedor(Long clienteProveedorId) {
		return validatorRegistry.getValidator(EmpresaValidator.class).validarClienteProveedor(clienteProveedorId);

	}

	public Estado validarEstadoGeneral(Long estadoId) {
		return validatorRegistry.getValidator(EstadoValidator.class).validarEstadoGeneral(estadoId);
	}

	public Estado validarEstadoParaOrdenCompra(Long estadoId) {
		return validatorRegistry.getValidator(EstadoValidator.class).validarEstadoParaOrdenCompra(estadoId);
	}

	public Estado validarEstadoParaPedido(Long estadoId) {
		return validatorRegistry.getValidator(EstadoValidator.class).validarEstadoParaPedido(estadoId);

	}

	public Estado validarEstadoParaFactura(Long estadoId) {
		return validatorRegistry.getValidator(EstadoValidator.class).validarEstadoParaFactura(estadoId);

	}

	public Estado validarEstadoParaCierre(Long estadoId) {
		return validatorRegistry.getValidator(EstadoValidator.class).validarEstadoParaCierre(estadoId);
	}

	public Almacen validarAlmacen(Long almacenId, Long empresaId) {
		return validatorRegistry.getValidator(AlmacenValidator.class).validarAlmacen(almacenId, empresaId);
	}

	public Produccion validarProduccion(Long produccionId, Long empresaId) {
		return validatorRegistry.getValidator(ProduccionValidator.class).validarProduccion(produccionId, empresaId);
	}

	public void validarFechasProduccion(Produccion produccion) {
		validatorRegistry.getValidator(ProduccionValidator.class).validarFechasDeProduccion(produccion);
	}

	public TipoMovimiento validarTipoMovimiento(Long tipoMovimientoId, Long empresaId) {
		return tipoMovimientoValidator.validarTipoMovimiento(tipoMovimientoId, empresaId);
	}

	public OrdenCompra validarOrdenCompra(Long ordenCompraId, Long empresaId) {
		return validatorRegistry.getValidator(OrdenCompraValidator.class).validarOrdenCompra(ordenCompraId, empresaId);
	}

	public Pedido validarPedido(Long pedidoId, Long empresaId) {
		return pedidoValidator.validarPedido(pedidoId, empresaId);
	}

	public Proveedor validarProveedor(Long proveedorId, Long empresaId) {
		return proveedorValidator.validarProveedor(proveedorId, empresaId);
	}

	public TipoProduccion validarTipoProduccion(Long tipoProduccion, Long empresaId) {
		return tipoProduccionValidator.validarTipoProduccion(tipoProduccion, empresaId);
	}

	public Espacio validarEspacio(Long espacioId, Long empresaId) {
		return validatorRegistry.getValidator(EspacioValidator.class).validarEspacio(espacioId, empresaId);
	}

	public Subseccion validarSubseccion(Long subseccionId, Long empresaId) {
		return subseccionValidator.validarSubseccion(subseccionId, empresaId);
	}

	public PresentacionProducto validarProductoPresentacion(Long productoPresentacionId, Long empresaId) {
		return productoPresentacionValidator.validarProductoPresentacion(productoPresentacionId, empresaId);
	}

	public ArticuloKardex validarArticuloKardex(Long articuloKardexId, Long empresaId) {
		return validatorRegistry.getValidator(ArticuloKardexValidator.class)
			.validarArticuloKardex(articuloKardexId, empresaId);
	}

	public PedidoCotizacion validarPedidoCotizacion(Long pedidoCotizacionId, Long empresaId) {
		return pedidoCotizacionValidator.validarPedidoCotizacion(pedidoCotizacionId, empresaId);
	}

	public Movimiento validarMovimiento(Long movimientoId) {
		return movimientoValidator.validarMovimiento(movimientoId);
	}

	public Evaluacion validarEvaluacion(Long evaluacionId, Long empresaId) {
		return evaluacionValidator.validarEvaluacionPorEmpresa(evaluacionId, empresaId);
	}

	public EvaluacionItem validarEvaluacionItem(Long evaItemId, Long empresaId) {
		return evaluacionItemValidator.validarEvaluacionItemPorEmpresa(evaItemId, empresaId);
	}

	public CriterioEvaluacion validarCriterioEvaluacion(Long criterioEvId, Long empresaId) {
		return criterioEvaluacionValidator.validarCriterioEvaluacionPorEmpresa(criterioEvId, empresaId);
	}

	public ProductoCategoria validarProductoCategoria(Long productoCategoriaId, Long empresaId) {
		return productoCategoriaValidator.validarProductoCategoriaPorEmpresa(productoCategoriaId, empresaId);
	}

	public TipoUnidad validarTipoUnidad(Long tipoUnidad) {
		return tipoUnidadValidator.validarTipoUnidad(tipoUnidad);
	}

	public void validarDuplicadoCierreInventario(Long empresaId, Long almacenId, LocalDate fechaInicio,
			LocalDate fechaCorte) {
		validatorRegistry.getValidator(CierreInventarioValidator.class)
			.validarDuplicado(empresaId, almacenId, fechaInicio, fechaCorte);
	}

	public CierreInventario validarCierreInventario(Long cierreId, Long empresaId) {
		return validatorRegistry.getValidator(CierreInventarioValidator.class)
			.validarCierreInventario(cierreId, empresaId);
	}

	public CierreInventarioDetalle validarDetalleCierreInventario(Long detalleId, Long empresaId) {
		return validatorRegistry.getValidator(CierreInventarioDetalleValidator.class)
			.validarCierreDetalle(detalleId, empresaId);
	}

	public EmpresaRol validarEmpresaRol(Long empresaRolId, Long empresaId) {
		return validatorRegistry.getValidator(EmpresaRolValidator.class).validarEmpresaRol(empresaRolId, empresaId);
	}

	public Rol validarRol(Long rolId) {
		return validatorRegistry.getValidator(RolValidator.class).validarRol(rolId);
	}

	public EmpresaRol validarEmpresaRolAdmin(Long empresaRolId) {
		return validatorRegistry.getValidator(EmpresaRolValidator.class).validarEmpresaRolAdmin(empresaRolId);
	}

	public EmpresaRol validarRolDeEmpresaActivo(Long empresaId, Long rolId) {
		return validatorRegistry.getValidator(EmpresaRolValidator.class).validarRolDeEmpresaActivo(empresaId, rolId);
	}

}
