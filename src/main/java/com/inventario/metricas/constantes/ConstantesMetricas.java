package com.inventario.metricas.constantes;

import java.util.Map;

public final class ConstantesMetricas {
    private ConstantesMetricas(){
        throw new IllegalStateException("Clase de constantes para metricas");
    }

    public static final Map<String, String> ENTITY_MAP = Map.ofEntries(
            Map.entry("pais", "Pais"),
            Map.entry("departamento", "Departamento"),
            Map.entry("municipio", "Municipio"),
            Map.entry("grupo", "Grupo"),
            Map.entry("tipoSede", "TipoSede"),
            Map.entry("sede", "Sede"),
            Map.entry("tipoBloque", "TipoBloque"),
            Map.entry("bloque", "Bloque"),
            Map.entry("tipoEspacio", "TipoEspacio"),
            Map.entry("espacio", "Espacio"),
            Map.entry("almacen", "Almacen"),
            Map.entry("pedido", "Pedido"),
            Map.entry("ordenCompra", "OrdenCompra"),
            Map.entry("ordenCompraItem", "ArticuloOrdenCompra"),
            Map.entry("kardex", "Kardex"),
            Map.entry("kardexItem", "ArticuloKardex")
    );
}
