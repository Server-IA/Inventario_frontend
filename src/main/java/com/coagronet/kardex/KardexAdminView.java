package com.coagronet.kardex;

import java.time.OffsetDateTime;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Immutable
@Subselect("""
        SELECT
            K.kar_id AS id,
            K.kar_fecha_hora AS fecha_hora,
            K.kar_tipo_movimiento_id AS tipo_movimiento_id,
            K.kar_estado_id AS estado_id,
            a.alm_nombre AS nombre_almacen,
            tm.tim_nombre AS nombre_tipo_movimiento,
            p.pro_nombre AS nombre_produccion,
            e.est_nombre AS nombre_estado,
            emp.emp_nombre AS nombre_empresa,
            emp_cli.emp_nombre AS nombre_cliente_proveedor,
            a_dest.alm_nombre AS nombre_almacen_destino
        FROM
            public.kardex K
            INNER JOIN public.almacen a ON K.kar_almacen_id = a.alm_id
            INNER JOIN public.tipo_movimiento tm ON K.kar_tipo_movimiento_id = tm.tim_id
            INNER JOIN public.estado e ON K.kar_estado_id = e.est_id
            INNER JOIN public.empresa emp ON K.kar_empresa_id = emp.emp_id
            LEFT JOIN public.produccion p ON K.kar_produccion_id = p.pro_id
            LEFT JOIN public.empresa emp_cli ON K.kar_cliente_proveedor_id = emp_cli.emp_id
            LEFT JOIN public.almacen a_dest ON K.kar_almacen_destino_id = a_dest.alm_id
                """)
@Getter
public class KardexAdminView {

    @Id
    private Long id;

    private OffsetDateTime fechaHora;

    @Column(name = "tipo_movimiento_id")
    private Long tipoMovimientoId;

    @Column(name = "estado_id")
    private Long estadoId;

    private String nombreAlmacen;
    private String nombreTipoMovimiento;
    private String nombreProduccion;
    private String nombreEstado;
    private String nombreEmpresa;
    private String nombreClienteProveedor;
    private String nombreAlmacenDestino;
}
