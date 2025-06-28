package com.coagronet.inventarioItem;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.inventario.Inventario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventario_item")
public class InventarioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventario_item_seq")
    @SequenceGenerator(name = "inventario_item_seq", sequenceName = "inventario_item_ini_id_seq", allocationSize = 1)
    @Column(name = "ini_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ini_inventario_id", referencedColumnName = "inv_id")
    private Inventario inventario;

    @Column(name = "ini_descripcion")
    private String descripcion;

    @Column(name = "ini_uuid")
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ini_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "ini_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "ini_producto_identificador_id", referencedColumnName = "kai_producto_identificador")
    private ArticuloKardex articuloKardex;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isBlank()) {
            this.uuid = UUID.randomUUID().toString();
        }
    }


}
