package com.coagronet.entidadvalidator;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.entidadvalidator.entidades.*;
import com.coagronet.estado.Estado;
import com.coagronet.kardex.Kardex;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.pedido.Pedido;
import com.coagronet.produccion.Produccion;
import com.coagronet.proveedor.Proveedor;
import com.coagronet.tipoMovimiento.TipoMovimiento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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

    public TipoMovimiento validarTipoMovimiento(Long tipoMovimientoId, Long empresaId) {
        return tipoMovimientoValidator.validarTipoMovimiento(tipoMovimientoId, empresaId);
    }

    public Kardex validarKardex(Long kardexId, Long empresaId) {
        return kardexValidator.validarKardex(kardexId, empresaId);
    }


    public OrdenCompra validarOrdenCompra(Long ordenCompraId, Long empresaId) {
        return ordenCompraValidator.validarOrdenCompra(ordenCompraId, empresaId);
    }

    public Pedido validarPedido(Long pedidoId, Long empresaId){
        return pedidoValidator.validarPedido(pedidoId, empresaId);
    }
    public Proveedor validarProveedor(Long proveedorId, Long empresaId){
        return proveedorValidator.validarProveedor(proveedorId, empresaId);
    }



}

