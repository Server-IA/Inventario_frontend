package com.coagronet.articuloInventario;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.UuidGenerator;

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

    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "ini_uuid", nullable = false, length = 36)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ini_empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ini_estado_id", nullable = false)
    private Estado estado;

    @Column(name = "ini_producto_identificador_id")
    private String identificadorProducto;

}
