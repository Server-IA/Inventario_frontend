package com.coagronet.presentacionProducto;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.marca.Marca;
import com.coagronet.presentacion.Presentacion;
import com.coagronet.producto.Producto;
import com.coagronet.unidad.Unidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto_presentacion", schema = "public")
public class PresentacionProducto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prp_generator")
    @SequenceGenerator(name = "prp_generator", sequenceName = "producto_presentacion_prp_id_seq", allocationSize = 1)
    @Column(name = "prp_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "prp_producto_id")
    private Producto producto;

    @Column(name = "prp_nombre")
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "prp_unidad_id")
    private Unidad unidad;

    @Column(name = "prp_descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "prp_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @Column(name = "prp_cantidad")
    private Double cantidad;

    @ManyToOne
    @JoinColumn(name = "prp_marca_id")
    private Marca marca;

    @ManyToOne
    @JoinColumn(name = "prp_presentacion_id")
    private Presentacion presentacion;

    @ManyToOne
    @JoinColumn(name = "prp_empresa_id")
    private Empresa empresa;

    @Column(name = "prp_desgregar")
    private Boolean desgregar;

}
