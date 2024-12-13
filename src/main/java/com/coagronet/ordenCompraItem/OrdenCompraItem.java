package com.coagronet.ordenCompraItem;

import com.coagronet.estado.Estado;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.productoPresentacion.ProductoPresentacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "orden_compra_item")
public class OrdenCompraItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_compra_item_generator")
    @SequenceGenerator(name = "orden_compra_item_generator", sequenceName = "orden_compra_item_id_seq", allocationSize = 1)
    @Column(name = "oci_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oci_orden_compra_id", referencedColumnName = "orc_id")
    private OrdenCompra ordenCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oci_producto_presentacion_id", referencedColumnName = "prp_id")
    private ProductoPresentacion productoPresentacion;

    @Column(name = "oci_cantidad")
    private Double cantidad;

    @Column(name = "oci_precio")
    private Double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oci_estado", referencedColumnName = "est_id")
    private Estado estado;

}
