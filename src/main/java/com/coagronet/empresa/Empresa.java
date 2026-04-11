package com.coagronet.empresa;

import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.coagronet.estado.Estado;
import com.coagronet.persona.Persona;
import com.coagronet.tipoIdentificacion.TipoIdentificacion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Entity
@Table(name = "empresa", schema = "public",
		uniqueConstraints = { @UniqueConstraint(name = "unique_emp_correo", columnNames = "emp_correo"),
				@UniqueConstraint(name = "unique_emp_identificacion", columnNames = "emp_identificacion"),
				@UniqueConstraint(name = "empresa_unique", columnNames = "emp_logo_hash") })
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empresa_generator")
	@SequenceGenerator(name = "empresa_generator", sequenceName = "empresa_emp_id_seq", allocationSize = 1)
	@Column(name = "emp_id", updatable = false, nullable = false)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_tipo_identificacion_id", referencedColumnName = "tii_id", nullable = false)
	private TipoIdentificacion tipoIdentificacion;

	@Column(name = "emp_identificacion", length = 50, nullable = false)
	private String identificacion;

	@Column(name = "emp_nombre", length = 100, nullable = false)
	private String nombre;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_persona_id", referencedColumnName = "per_id", nullable = false)
	private Persona persona;

	@Column(name = "emp_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@Column(name = "emp_celular", length = 13)
	private String celular;

	@Column(name = "emp_correo", length = 255, nullable = false)
	private String correo;

	@Column(name = "emp_contacto", length = 255)
	private String contacto;

	@Column(name = "emp_logo", length = 1024)
	private String logo;

	@Column(name = "emp_logo_hash", length = 264)
	private String logoHash;

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		Empresa empresa = (Empresa) o;
		return getId() != null && Objects.equals(getId(), empresa.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}