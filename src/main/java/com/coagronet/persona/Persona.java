package com.coagronet.persona;

import java.time.LocalDate;

import com.coagronet.estado.Estado;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;

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
@Table(name = "persona")
public class Persona {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persona_generator")
	@SequenceGenerator(name = "persona_generator", sequenceName = "persona_per_id_seq", allocationSize = 1)
	@Column(name = "per_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_tipo_identificacion_id", referencedColumnName = "tii_id")
	private TipoIdentificacion tipoIdentificacion;

	@Column(name = "per_identificacion", length = 255)
	private String identificacion;

	@Column(name = "per_nombre", length = 255)
	private String nombre;

	@Column(name = "per_apellido", length = 255)
	private String apellido;

	@Column(name = "per_genero", length = 255)
	private String genero;

	@Column(name = "per_fecha_nacimiento")
	private LocalDate fechaNacimiento;

	@Column(name = "per_estrato")
	private Integer estrato;

	@Column(name = "per_direccion", length = 255)
	private String direccion;

	@Column(name = "per_email_personal", length = 100)
	private String email;

	@Column(name = "per_celular", length = 255)
	private String celular;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "per_estado_id", referencedColumnName = "est_id")
	private Estado estado;

}