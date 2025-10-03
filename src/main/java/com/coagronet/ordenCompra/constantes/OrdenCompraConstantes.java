package com.coagronet.ordenCompra.constantes;

public final class OrdenCompraConstantes {
    private OrdenCompraConstantes(){
        throw new IllegalStateException("Clase de constantes para orden compra");
    }

    public static final Long ESTADO_ORDEN_COMPRA_INICIAL_ACTIVO = 23L;
    public static final Long ESTADO_ORDEN_COMPRA_ENTREGADO_AL_PROVEEDOR = 24L;
    public static final Long ESTADO_ORDEN_COMPRA_ENTREGA_PARCIAL = 25L;
    public static final Long ESTADO_ORDEN_COMPRA_ENTRADA_TOTAL = 26L;
}
