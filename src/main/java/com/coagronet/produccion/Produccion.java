package com.coagronet.produccion;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;

import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
import com.coagronet.estado.Estado;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.tipoProduccion.TipoProduccion;
// IMPORTANTE: Asegúrate de importar tu entidad Variedad
import com.coagronet.variedad.Variedad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
@Entity
@DynamicInsert
@Table(name = "produccion", schema = "public",
		indexes = { @Index(name = "idx_pro_empresa_id", columnList = "pro_empresa_id"),
				@Index(name = "idx_pro_estado_id", columnList = "pro_estado_id"),
				@Index(name = "idx_pro_espacio_id", columnList = "pro_espacio_id"),
				@Index(name = "idx_pro_sub_seccion_id", columnList = "pro_sub_seccion_id"),
				@Index(name = "idx_pro_tipo_produccion_id", columnList = "pro_tipo_produccion_id") })
public class Produccion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produccion_generator")
	@SequenceGenerator(name = "produccion_generator", sequenceName = "produccion_pro_id_seq", allocationSize = 1)
	@Column(name = "pro_id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "pro_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "pro_descripcion", length = 2048)
	private String descripcion;

	@Column(name = "pro_fecha_inicio")
	private LocalDateTime fechaInicio;

	@Column(name = "pro_fecha_final")
	private LocalDateTime fechaFinal;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_tipo_produccion_id", referencedColumnName = "tip_id", nullable = false)
	private TipoProduccion tipoProduccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_espacio_id", referencedColumnName = "esp_id", nullable = false)
	private Espacio espacio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_sub_seccion_id", referencedColumnName = "sus_id", nullable = false)
	private Subseccion subSeccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_variedad_id", referencedColumnName = "var_id")
	private Variedad variedad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id", nullable = false, insertable = false,
			updatable = false)
	private Empresa empresa;

	@TenantId
	@Column(name = "pro_empresa_id", nullable = false, updatable = false)
	private Long tenantEmpresaId;

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
		Produccion produccion = (Produccion) o;
		return getId() != null && Objects.equals(getId(), produccion.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}