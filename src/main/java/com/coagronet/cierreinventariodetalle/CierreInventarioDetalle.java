package com.coagronet.cierreinventariodetalle;

import com.coagronet.almacen.Almacen;
import com.coagronet.cierreinventario.CierreInventario;
import com.coagronet.empresa.Empresa;
import com.coagronet.presentacionProducto.PresentacionProducto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cierre_inventario_detalle", schema = "public")
public class CierreInventarioDetalle {

    @Id
    @SequenceGenerator(name = "cierre_inventario_detalle_generator",
    sequenceName = "cierre_inventario_detalle_cid_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cierre_inventario_detalle_generator")
    @Column(name = "cid_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cid_cierre_inventario_id", referencedColumnName = "cii_id")
    private CierreInventario cierreInventario;

    @ManyToOne
    @JoinColumn(name = "cid_producto_presentacion_id", referencedColumnName = "prp_id")
    private PresentacionProducto presentacionProducto;

    @Column(name = "cid_stock")
    private BigDecimal stock;

    @Column(name = "cid_fecha_corte")
    private LocalDate fechaCorte;

    @ManyToOne
    @JoinColumn(name = "cid_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "cid_almacen_id", referencedColumnName = "alm_id")
    private Almacen almacen;

}
