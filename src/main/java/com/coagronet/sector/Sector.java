package com.coagronet.sector;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.variedad.Variedad;

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
@Table(name = "sector", schema = "iot")
public class Sector {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sector_generator")
	@SequenceGenerator(name = "sector_generator", sequenceName = "iot.sector_sec_id_seq", allocationSize = 1)
	@Column(name = "sec_id", nullable = false)
	private Long id;

	@Column(name = "sec_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne
	@JoinColumn(name = "sec_sub_seccion_id", referencedColumnName = "sus_id", nullable = false)
	private Subseccion subseccion;

	@ManyToOne
	@JoinColumn(name = "sec_variedad_id", referencedColumnName = "var_id", nullable = false)
	private Variedad variedad;

	@ManyToOne
	@JoinColumn(name = "sec_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne
	@JoinColumn(name = "sec_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

}
