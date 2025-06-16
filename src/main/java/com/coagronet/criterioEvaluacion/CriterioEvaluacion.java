package com.coagronet.criterioEvaluacion;

import com.coagronet.estado.Estado;
import com.coagronet.tipoEvaluacion.TipoEvaluacion;

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
@Table(name = "criterio_evaluacion")
public class CriterioEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "criterio_evaluacion_generator")
    @SequenceGenerator(name = "criterio_evaluacion_generator", sequenceName = "criterio_evaluacion_cre_id_seq", allocationSize = 1)
    @Column(name = "cre_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cre_tipo_evaluacion_id", referencedColumnName = "tie_id")
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "cre_nombre", length = 255)
    private String nombre;

    @Column(name = "cre_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cre_estado_id", referencedColumnName = "est_id")
    private Estado estado;
}
