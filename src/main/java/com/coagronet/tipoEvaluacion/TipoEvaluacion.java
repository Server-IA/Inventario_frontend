package com.coagronet.tipoEvaluacion;

import com.coagronet.estado.Estado;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "tie_nombre", length = 255)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tie_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    // añadir el campo empresa a cada tabla tipo y setear empresaId a 0
    // @ManyToOne
    // @JoinColumn(name="tie_empresa_id", referencedColumnName = "emp_id")
    // private Empresa empresa;
}
