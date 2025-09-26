package com.coagronet.entidadvalidator.constantes;

public final class EntidadValidatorConstantes {

    private EntidadValidatorConstantes(){
        throw new IllegalStateException("Clase de constantes para las validaciones de entidad");
    }

    public static final String EMPRESA_NO_ENCONTRADA = "Empresa no encontrada";
    public static final String ESTADO_NO_VALIDO = "El estado no es válido";
    public static final String ALMACEN_NO_VALIDO = "El almacén no es válido para esta empresa";
    public static final String PRODUCCION_NO_VALIDO = "La producción no es válida para esta empresa";
    public static final String TIPO_MOVIMIENTO_NO_VALIDO = "El tipo de movimiento no es válido para esta empresa";
    public static final String KARDEX_NO_VALIDO = "El kardex no es válido para esta empresa";
    public static final String CLIENTE_PROVEEDOR_NO_VALIDO = "Cliente/Proveedor no encontrado";

}
