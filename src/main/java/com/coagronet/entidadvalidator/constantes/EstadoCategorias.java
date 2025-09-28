package com.coagronet.entidadvalidator.constantes;

public final class EstadoCategorias {
    private EstadoCategorias(){
        throw new IllegalStateException("Clase de constantes para las categorias de estado");
    }

    public static final Long GENERAL = 1L;
    public static final Long PEDIDO = 2L;
    public static final Long ORDEN_COMPRA = 3L;
    public static final Long FACTURA = 4L;
}
