package com.coagronet.tipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "tipo_movimiento")
public class TipoMovimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_movimiento_generator")
    @SequenceGenerator(name = "tipo_movimiento_generator", sequenceName = "tipo_movimiento_tim_id_seq", allocationSize = 1)
    @Column(name = "tim_id", nullable = false)
    private Integer id;

    @Column(name = "tim_nombre", length = 255)
    private String nombre;

    @Column(name = "tim_descripcion", length = 500)
    private String descripcion;

    @Column(name = "tim_estado", columnDefinition = "integer default 1")
    private Integer estado;

}
