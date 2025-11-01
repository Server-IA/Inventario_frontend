package com.coagronet.validator;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.evaluacion.Evaluacion;
import com.coagronet.evaluacionitem.EvaluacionItem;
import com.coagronet.tipounidad.TipoUnidad;
import com.coagronet.validator.inventario.entidades.*;
import com.coagronet.validator.parametrizacion.entidades.*;
import org.springframework.stereotype.Component;

import com.coagronet.almacen.Almacen;
import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
import com.coagronet.movimiento.Movimiento;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.estado.Estado;
import com.coagronet.kardex.Kardex;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.pedido.Pedido;
import com.coagronet.pedidocotizacion.PedidoCotizacion;
import com.coagronet.produccion.Produccion;
import com.coagronet.productoCategoria.ProductoCategoria;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.tipoMovimiento.TipoMovimiento;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntidadValidatorFacade {

    private final EmpresaValidator empresaValidator;
    private final EstadoValidator estadoValidator;
    private final AlmacenValidator almacenValidator;
    private final ProduccionValidator produccionValidator;
    private final TipoMovimientoValidator tipoMovimientoValidator;
    private final KardexValidator kardexValidator;
    private final OrdenCompraValidator ordenCompraValidator;
    private final PedidoValidator pedidoValidator;
    private final ProveedorValidator proveedorValidator;
    private final TipoProduccionValidator tipoProduccionValidator;
    private final EspacioValidator espacioValidator;
    private final SubseccionValidator subseccionValidator;
    private final ProductoPresentacionValidator productoPresentacionValidator;
    private final ArticuloKardexValidator articuloKardexValidator;
    private final MovimientoValidator movimientoValidator;
    private final PedidoCotizacionValidator pedidoCotizacionValidator;
    private final EvaluacionValidator evaluacionValidator;
    private final CriterioEvaluacionValidator criterioEvaluacionValidator;
    private final EvaluacionItemValidator evaluacionItemValidator;
    private final ProductoCategoriaValidator productoCategoriaValidator;
    private final TipoUnidadValidator tipoUnidadValidator;

    public Empresa validarEmpresa(Long empresaId) {
        return empresaValidator.validarEmpresa(empresaId);
    }

    public Empresa validarClienteProveedor(Long clienteProveedorId) {
        return empresaValidator.validarClienteProveedor(clienteProveedorId);
    }

    public Estado validarEstadoGeneral(Long estadoId) {
        return estadoValidator.validarEstadoGeneral(estadoId);
    }

    public Estado validarEstadoParaOrdenCompra(Long estadoId) {
        return estadoValidator.validarEstadoParaOrdenCompra(estadoId);
    }

    public Estado validarEstadoParaPedido(Long estadoId) {
        return estadoValidator.validarEstadoParaPedido(estadoId);

    }

    public Estado validarEstadoParaFactura(Long estadoId) {
        return estadoValidator.validarEstadoParaFactura(estadoId);

    }

    public Almacen validarAlmacen(Long almacenId, Long empresaId) {
        return almacenValidator.validarAlmacen(almacenId, empresaId);
    }

    public Produccion validarProduccion(Long produccionId, Long empresaId) {
        return produccionValidator.validarProduccion(produccionId, empresaId);
    }

    public void validarFechasProduccion(Produccion produccion) {
        produccionValidator.validarFechasDeProduccion(produccion);
    }

    public TipoMovimiento validarTipoMovimiento(Long tipoMovimientoId, Long empresaId) {
        return tipoMovimientoValidator.validarTipoMovimiento(tipoMovimientoId, empresaId);
    }

    public Kardex validarKardex(Long kardexId, Long empresaId) {
        return kardexValidator.validarKardex(kardexId, empresaId);
    }

    public OrdenCompra validarOrdenCompra(Long ordenCompraId, Long empresaId) {
        return ordenCompraValidator.validarOrdenCompra(ordenCompraId, empresaId);
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
        return espacioValidator.validarEspacio(espacioId, empresaId);
    }

    public Subseccion validarSubseccion(Long subseccionId, Long empresaId) {
        return subseccionValidator.validarSubseccion(subseccionId, empresaId);
    }

    public Kardex validarKardexPorOrdenCompra(Long ordenCompraId, Long empresaId) {
        return kardexValidator.validarKardexPorOrdenCompra(ordenCompraId, empresaId);
    }

    public PresentacionProducto validarProductoPresentacion(Long productoPresentacionId, Long empresaId) {
        return productoPresentacionValidator.validarProductoPresentacion(productoPresentacionId, empresaId);
    }

    public ArticuloKardex validarArticuloKardex(Long articuloKardexId, Long empresaId) {
        return articuloKardexValidator.validarArticuloKardex(articuloKardexId, empresaId);
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

    public TipoUnidad validarTipoUnidad(Long tipoUnidad){
        return tipoUnidadValidator.validarTipoUnidad(tipoUnidad);
    }

}
