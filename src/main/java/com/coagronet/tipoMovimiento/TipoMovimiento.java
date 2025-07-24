package com.coagronet.tipoMovimiento;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;

import com.coagronet.movimiento.Movimiento;
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
@Table(name = "tipo_movimiento")
public class TipoMovimiento {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_movimiento_generator")
	@SequenceGenerator(name = "tipo_movimiento_generator", sequenceName = "tipo_movimiento_tim_id_seq",
			allocationSize = 1)
	@Column(name = "tim_id", nullable = false)
	private Long id;

	@Column(name = "tim_nombre", length = 255)
	private String nombre;

	@Column(name = "tim_descripcion", length = 500)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tim_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tim_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tim_movimiento_id", referencedColumnName = "mov_id")
	private Movimiento movimiento;

}
