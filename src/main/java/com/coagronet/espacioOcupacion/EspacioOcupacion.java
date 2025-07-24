package com.coagronet.espacioOcupacion;

import java.time.LocalDateTime;

import com.coagronet.ocupacion.Ocupacion;
import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
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
@Table(name = "espacio_ocupacion")
public class EspacioOcupacion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "espacio_ocupacion_generator")
	@SequenceGenerator(name = "espacio_ocupacion_generator", sequenceName = "espacio_ocupacion_eso_id_seq",
			allocationSize = 1)
	@Column(name = "eso_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eso_espacio_id", referencedColumnName = "esp_id")
	private Espacio espacio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eso_actividad_ocupacion_id", referencedColumnName = "aco_id")
	private Ocupacion ocupacion;

	@Column(name = "eso_fecha_inicio")
	private LocalDateTime fechaInicio;

	@Column(name = "eso_fecha_fin")
	private LocalDateTime fechaFin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eso_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eso_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
