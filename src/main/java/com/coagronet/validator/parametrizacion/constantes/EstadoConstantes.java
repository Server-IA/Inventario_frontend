package com.coagronet.validator.parametrizacion.constantes;

public final class EstadoConstantes {
    private EstadoConstantes(){
        throw new IllegalStateException("Clase de constantes para los estados");
    }

    public static final Long ESTADO_GENERAL_ACTIVO = 1L;

    public static final Long ESTADO_CIERRE_ABIERTO = 35L;
    public static final Long ESTADO_CIERRE_CERRADO = 36L;
}
