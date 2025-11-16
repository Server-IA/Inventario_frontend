package com.coagronet.validator;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.evaluacion.Evaluacion;
import com.coagronet.evaluacionitem.EvaluacionItem;
import com.coagronet.tipounidad.TipoUnidad;
import com.coagronet.validator.common.ValidatorRegistry;
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

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class EntidadValidatorFacade {

    private final ValidatorRegistry validatorRegistry;

    private final TipoMovimientoValidator tipoMovimientoValidator;
    private final KardexValidator kardexValidator;
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

    public Kardex validarKardex(Long kardexId, Long empresaId) {
        return kardexValidator.validarKardex(kardexId, empresaId);
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

    public Kardex validarKardexPorOrdenCompra(Long ordenCompraId, Long empresaId) {
        return kardexValidator.validarKardexPorOrdenCompra(ordenCompraId, empresaId);
    }

    public PresentacionProducto validarProductoPresentacion(Long productoPresentacionId, Long empresaId) {
        return productoPresentacionValidator.validarProductoPresentacion(productoPresentacionId, empresaId);
    }

    public ArticuloKardex validarArticuloKardex(Long articuloKardexId, Long empresaId) {
        return validatorRegistry.getValidator(ArticuloKardexValidator.class).validarArticuloKardex(articuloKardexId, empresaId);
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

    public void validarDuplicadoCierreInventario(Long empresaId, Long almacenId, LocalDate fechaInicio, LocalDate fechaCorte) {
        validatorRegistry.getValidator(CierreInventarioValidator.class).validarDuplicado(empresaId, almacenId, fechaInicio, fechaCorte);
    }

}
