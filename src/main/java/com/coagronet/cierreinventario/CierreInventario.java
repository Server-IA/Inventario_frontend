package com.coagronet.cierreinventario;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cierre_inventario", schema = "public")
public class CierreInventario {

    @Id
    @SequenceGenerator(name = "cierre_inventario_generator",
            sequenceName = "cierre_inventario_cii_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cierre_inventario_generator")
    @Column(name = "cii_id")
    private Long id;

    @Column(name = "cii_fecha_corte")
    private LocalDate fechaCorte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cii_almacen_id", referencedColumnName = "alm_id")
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cii_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cii_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @Column(name = "cii_fecha_creacion", insertable = false, updatable = false)
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cii_usuario_id", referencedColumnName = "usu_id")
    private User usuario;

    @Column(name = "cii_descripcion")
    private String descripcion;




}
