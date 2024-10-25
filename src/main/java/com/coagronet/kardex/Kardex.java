package com.coagronet.kardex;

import java.time.LocalDateTime;

import com.coagronet.almacen.Almacen;
import com.coagronet.produccion.Produccion;
import com.coagronet.tipoMovimiento.TipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "kardex")
public class Kardex {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kardex_generator")
    @SequenceGenerator(name = "kardex_generator", sequenceName = "kardex_kar_id_seq", allocationSize = 1)
    @Column(name = "kar_id", nullable = false)
    public Integer id;

    @Column(name = "kar_fecha_hora")
    public LocalDateTime fechaHora;

    @ManyToOne
    @JoinColumn(name = "kar_almacen_id", referencedColumnName = "alm_id")
    public Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "kar_produccion_id", referencedColumnName = "pro_id")
    public Produccion produccion;

    @ManyToOne
    @JoinColumn(name = "kar_tipo_movimiento_id", referencedColumnName = "tim_id")
    public TipoMovimiento tipoMovimiento;

    @Column(name = "kar_descripcion", length = 500)
    public String descripcion;

    @Column(name = "kar_estado", columnDefinition = "integer default 1")
    private Integer estado;

}
