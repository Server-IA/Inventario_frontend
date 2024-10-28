package com.coagronet.tipoProduccion;

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
@Table(name = "tipo_produccion")
public class TipoProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_produccion_generator")
    @SequenceGenerator(name = "tipo_produccion_generator", sequenceName = "tipo_produccion_tip_id_seq", allocationSize = 1)
    @Column(name = "tip_id", nullable = false)
    private Integer id;

    @Column(name = "tip_nombre", length = 100)
    private String nombre;

    @Column(name = "tip_descripcion", length = 255)
    private String descripcion;

    @Column(name = "tip_estado", columnDefinition = "integer default 1")
    private Integer estado;

}
