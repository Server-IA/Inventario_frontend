package com.coagronet.proceso;

import com.coagronet.estado.Estado;
import com.coagronet.tipoProduccion.TipoProduccion;

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
@Table(name = "proceso")
public class Proceso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proceso_generator")
    @SequenceGenerator(name = "proceso_generator", sequenceName = "proceso_pro_id_seq", allocationSize = 1)
    @Column(name = "pro_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_tipo_produccion_id", referencedColumnName = "tip_id")
    private TipoProduccion tipoProduccion;

    @Column(name = "pro_nombre", length = 100)
    private String nombre;

    @Column(name = "pro_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pro_estado", referencedColumnName = "est_id")
    private Estado estado;
}
