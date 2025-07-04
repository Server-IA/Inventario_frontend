package com.coagronet.controlInventario;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.time.LocalDateTime;

@Entity
@Table(name = "vista_kardex", schema = "public")
@Immutable
@Data
public class VistaKardex {

    @Id
    @Column(name = "kai_id")
    private Long kaiId;

    @Column(name = "kar_fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "prp_producto_id")
    private Long productoId;

    @Column(name = "pro_nombre")
    private String nombreProducto;

    @Column(name = "kar_tipo_movimiento_id")
    private Long tipoMovimientoId;

    @Column(name = "prp_cantidad")
    private Double cantidadPresentacion;

    @Column(name = "uni_nombre")
    private String unidadNombre;

    @Column(name = "emp_id")
    private Long empresaId;

    @Column(name = "emp_nombre")
    private String empresaNombre;

    @Column(name = "emp_correo")
    private String empresaCorreo;

    @Column(name = "emp_contacto")
    private String empresaContacto;

    @Column(name = "emp_logo")
    private String empresaLogo;

    @Column(name = "sed_id")
    private Long sedeId;

    @Column(name = "sed_nombre")
    private String sedeNombre;

    @Column(name = "alm_id")
    private Long almacenId;

    @Column(name = "alm_nombre")
    private String almacenNombre;

    @Column(name = "mun_nombre")
    private String municipioNombre;

    @Column(name = "blo_nombre")
    private String bloqueNombre;

    @Column(name = "esp_nombre")
    private String espacioNombre;

    @Column(name = "cantidad")
    private Double cantidad;

    @Column(name = "entrada")
    private Double entrada;

    @Column(name = "salida")
    private Double salida;
}

