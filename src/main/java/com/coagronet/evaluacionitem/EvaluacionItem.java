package com.coagronet.evaluacionitem;

import com.coagronet.criterioEvaluacion.CriterioEvaluacion;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.evaluacion.Evaluacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "evaluacion_item")
@Table(name = "evaluacion_item", schema = "public")
public class EvaluacionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluacion_item_seq")
    @SequenceGenerator(name = "evaluacion_item_seq", sequenceName = "evaluacion_item_evi_id_seq", allocationSize = 1)
    @Column(name = "evi_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evi_evaluacion_id", referencedColumnName = "eva_id")
    private Evaluacion evaluacion;

    @Column(name = "evi_valor")
    private Integer valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evi_criterio_evaluacion_id", referencedColumnName = "cre_id")
    private CriterioEvaluacion criterioEvaluacion;

    @Column(name = "evi_descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evi_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evi_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;





}
