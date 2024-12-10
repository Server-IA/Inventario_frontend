package com.coagronet.kardexItem;

import com.coagronet.estado.Estado;
import com.coagronet.kardex.Kardex;
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
@Table(name = "kardex_item")
public class KardexItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kardex_item_generator")
    @SequenceGenerator(name = "kardex_item_generator", sequenceName = "kardex_item_kai_id_seq", allocationSize = 1)
    @Column(name = "kai_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kai_kardex_id", referencedColumnName = "kar_id")
    private Kardex kardex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kai_producto_presentacion_id", referencedColumnName = "prp_id")
    private ProductoPresentacion productoPresentacion;

    @Column(name = "kai_cantidad")
    private Double cantidad;

    @Column(name = "kai_precio")
    private Double precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kai_estado", referencedColumnName = "est_id")
    private Estado estado;

}
