package com.coagronet.evaluacion;

import java.time.LocalDateTime;

import com.coagronet.empresa.Empresa;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "evaluacion", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaSeqGen")
    @SequenceGenerator(name = "evaSeqGen", sequenceName = "evaluacion_eva_id_seq", allocationSize = 1)
    @Column(name = "eva_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eva_tipo_evaluacion_id", referencedColumnName = "tie_id")
    private TipoEvaluacion tipoEvaluacion;

    @Column(name = "eva_fecha_hora")
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eva_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @Column(name = "eva_evaluado")
    private Integer evaluado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eva_estado_id", referencedColumnName = "est_id")
    private Estado estado;
}
