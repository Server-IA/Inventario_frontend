package com.coagronet.produccion;

import java.time.LocalDateTime;

import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
import com.coagronet.estado.Estado;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.tipoProduccion.TipoProduccion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "produccion")
public class Produccion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produccion_generator")
	@SequenceGenerator(name = "produccion_generator", sequenceName = "produccion_pro_id_seq", allocationSize = 1)
	@Column(name = "pro_id")
	private Long id;

	@Column(name = "pro_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "pro_descripcion", length = 2048)
	private String descripcion;

	@Column(name = "pro_fecha_inicio")
	private LocalDateTime fechaInicio;

	@Column(name = "pro_fecha_final")
	private LocalDateTime fechaFinal;

	@ManyToOne
	@JoinColumn(name = "pro_tipo_produccion_id", referencedColumnName = "tip_id", nullable = false)
	private TipoProduccion tipoProduccion;

	@ManyToOne
	@JoinColumn(name = "pro_espacio_id", referencedColumnName = "esp_id", nullable = false)
	private Espacio espacio;

	@ManyToOne
	@JoinColumn(name = "pro_sub_seccion_id", referencedColumnName = "sus_id", nullable = false)
	private Subseccion subSeccion;

	@ManyToOne
	@JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne
	@JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

}
