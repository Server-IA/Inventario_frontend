package com.inventario.criterioEvaluacion;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.tipoEvaluacion.TipoEvaluacion;

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
	@SequenceGenerator(name = "criterio_evaluacion_generator", sequenceName = "criterio_evaluacion_cre_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "criterio_evaluacion_generator")
	@Column(name = "cre_id")
	private Long id;

	@Column(name = "cre_nombre", length = 255, nullable = false)
	private String nombre;

	@Column(name = "cre_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cre_tipo_evaluacion_id", referencedColumnName = "tie_id", nullable = false)
	private TipoEvaluacion tipoEvaluacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cre_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cre_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

}
