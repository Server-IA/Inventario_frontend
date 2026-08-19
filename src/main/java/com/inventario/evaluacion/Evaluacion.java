package com.inventario.evaluacion;

import java.time.LocalDateTime;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.tipoEvaluacion.TipoEvaluacion;

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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "evaluacion")
@Table(name = "evaluacion", schema = "public")
public class Evaluacion {

	@Id
	@SequenceGenerator(name = "evaluacion_sequence_generator", sequenceName = "evaluacion_eva_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluacion_sequence_generator")
	@Column(name = "eva_id")
	private Long id;

	@Column(name = "eva_fecha_hora")
	private LocalDateTime fechaHora;

	@Column(name = "eva_evaluado")
	private Integer idEntidadEvaluada;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eva_tipo_evaluacion_id", referencedColumnName = "tie_id", nullable = false,
			foreignKey = @ForeignKey(name = "evaluacion_eva_tipo_evaluacion_id_fkey"))
	private TipoEvaluacion tipoEvaluacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eva_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "evaluacion_eva_estado_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eva_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "evaluacion_eva_empresa_id_fkey"))
	private Empresa empresa;

}
