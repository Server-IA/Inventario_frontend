package com.coagronet.productopresentacionstock;

import com.coagronet.empresa.Empresa;
import com.coagronet.presentacionProducto.PresentacionProducto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "producto_presentacion_stock")
public class ProductoPresentacionStock {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_presentacion_stock_generator")
    @SequenceGenerator(name = "producto_presentacion_stock_generator",
            sequenceName = "producto_presentacion_stock_pps_id_seq", allocationSize = 1)
    @Column(name = "pps_id")
    private Long id;

    @Column(name = "pps_stock")
    private BigDecimal stock;

    @Column(name = "pps_fecha_hora")
    private LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "pps_producto_presentacion_id")
    private PresentacionProducto productoPresentacion;

    @ManyToOne
    @JoinColumn(name = "pps_empresa_id")
    private Empresa empresa;

}
