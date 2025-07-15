package com.coagronet.articuloInventario;

import java.io.Serializable;
import java.util.UUID;

import com.coagronet.articuloKardex.ArticuloKardex;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.inventario.Inventario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario_item", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArticuloInventario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventario_item_seq")
    @SequenceGenerator(name = "inventario_item_seq", sequenceName = "inventario_item_ini_id_seq", allocationSize = 1)
    @EqualsAndHashCode.Include
    @Column(name = "ini_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ini_inventario_id", nullable = false)
    private Inventario inventario;

    @Column(name = "ini_descripcion", length = 2048)
    private String descripcion;

    @Column(name = "ini_uuid", nullable = false, unique = true)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ini_empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ini_estado_id", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ini_producto_identificador_id", referencedColumnName = "kai_producto_identificador", insertable = false, updatable = false, nullable = true)
    private ArticuloKardex articuloKardex;

    @PrePersist
    public void prePersist() {
        if (uuid == null || uuid.isBlank()) {
            this.uuid = UUID.randomUUID().toString();
        }
    }

}
