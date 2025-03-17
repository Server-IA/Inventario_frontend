package com.coagronet.tipoEvaluacion;

import com.coagronet.estado.Estado;

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
@Table(name = "tipo_evaluacion")
public class TipoEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_evaluacion_generator")
    @SequenceGenerator(
            name = "tipo_evaluacion_generator",
            sequenceName = "tipo_evaluacion_tie_id_seq",
            allocationSize = 1
    )
    @Column(name = "tie_id", nullable = false)
    private Integer id;

    @Column(name = "tie_nombre", length = 255)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tie_estado", referencedColumnName = "est_id")
    private Estado estado;
}
