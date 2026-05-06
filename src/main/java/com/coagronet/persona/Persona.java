package com.coagronet.persona;

import java.time.LocalDate;

import com.coagronet.estado.Estado;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;

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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persona", schema = "public", uniqueConstraints = {
		@UniqueConstraint(name = "unique_per_nombre_per_apellido_per_identificacion", columnNames = { "per_nombre",
				"per_apellido", "per_identificacion" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persona_seq_gen")
	@SequenceGenerator(name = "persona_seq_gen", sequenceName = "persona_per_id_seq", allocationSize = 1)
	@Column(name = "per_id", nullable = false, updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "per_tipo_identificacion_id", foreignKey = @ForeignKey(name = "persona_per_tipo_identificacion_id_fkey"), nullable = false)
	private TipoIdentificacion tipoIdentificacion;

	@Column(name = "per_identificacion", nullable = false, length = 50)
	private String identificacion;

	@Column(name = "per_nombre", nullable = false, length = 100)
	private String nombre;

	@Column(name = "per_apellido", nullable = false, length = 100)
	private String apellido;

	@Column(name = "per_genero", length = 100)
	private String genero;

	@Column(name = "per_fecha_nacimiento")
	private LocalDate fechaNacimiento;

	@Column(name = "per_estrato")
	private Integer estrato;

	@Column(name = "per_direccion", length = 255)
	private String direccion;

	@Column(name = "per_email_personal", nullable = false, length = 100, unique = true)
	private String emailPersonal;

	@Column(name = "per_celular", length = 13)
	private String celular;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "per_estado_id", foreignKey = @ForeignKey(name = "persona_per_estado_id_fkey"), nullable = false, columnDefinition = "bigint default 1")
	private Estado estado;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof Persona persona))
			return false;
		return id != null && id.equals(persona.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}