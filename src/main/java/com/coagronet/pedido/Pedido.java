package com.coagronet.pedido;

import java.time.LocalDateTime;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.produccion.Produccion;

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
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_generator")
    @SequenceGenerator(name = "pedido_generator", sequenceName = "pedido_id_seq", allocationSize = 1)
    @Column(name = "ped_id", nullable = false)
    private Long id;

    @Column(name = "ped_fecha_hora")
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ped_almacen_id", referencedColumnName = "alm_id")
    private Almacen almacen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ped_produccion_id", referencedColumnName = "pro_id")
    private Produccion produccion;

    @Column(name = "ped_descripcion", length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ped_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ped_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

}
