package com.coagronet.pedidoItem;

import com.coagronet.estado.Estado;
import com.coagronet.pedido.Pedido;
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
@Table(name = "pedido_item")
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_item_generator")
    @SequenceGenerator(name = "pedido_item_generator", sequenceName = "pedido_item_pei_id_seq", allocationSize = 1)
    @Column(name = "pei_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pei_pedido_id", referencedColumnName = "ped_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pei_producto_presentacion_id", referencedColumnName = "prp_id")
    private ProductoPresentacion productoPresentacion;

    @Column(name = "pei_cantidad")
    private Double cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pei_estado", referencedColumnName = "est_id")
    private Estado estado;

}
