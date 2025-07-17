package com.coagronet.tipoEvaluacion;

import com.coagronet.estado.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
@Table(name = "tipo_evaluacion", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tieSeqGen")
    @SequenceGenerator(name = "tieSeqGen", sequenceName = "tipo_evaluacion_tie_id_seq", allocationSize = 1)
    @Column(name = "tie_id")
    private Long id;

    @Column(name = "tie_nombre", length = 100)
    private String nombre;

    @Column(name = "tie_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tie_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "tipo_evaluacion_tie_estado_fkey"))
    private Estado estado;

    // añadir el campo empresa a cada tabla tipo y setear empresaId a 0
    // @ManyToOne
    // @JoinColumn(name="tie_empresa_id", referencedColumnName = "emp_id")
    // private Empresa empresa;
}
