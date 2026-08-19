package com.inventario.almacen;

import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;

import com.inventario.empresa.Empresa;
import com.inventario.espacio.Espacio;
import com.inventario.estado.Estado;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "almacen", schema = "public")
public class Almacen {

	@Id
	@SequenceGenerator(name = "almacen_generator", sequenceName = "almacen_alm_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "almacen_generator")
	@Column(name = "alm_id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "alm_nombre", length = 100, nullable = false)
	private String nombre;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "alm_geolocalizacion2", columnDefinition = "json")
	private String geolocalizacion2;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "alm_coordenadas2", columnDefinition = "json")
	private String coordenadas2;

	@Column(name = "alm_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alm_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@Column(name = "alm_geolocalizacion", length = 255)
	private String geolocalizacion;

	@Column(name = "alm_coordenadas", columnDefinition = "text")
	private String coordenadas;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alm_espacio_id", referencedColumnName = "esp_id", nullable = false)
	private Espacio espacio;

	@Column(name = "alm_direccion", length = 100)
	private String direccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alm_empresa_id", referencedColumnName = "emp_id", nullable = false, insertable = false,
			updatable = false)
	private Empresa empresa;

	@TenantId
	@Column(name = "alm_empresa_id", nullable = false)
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
		Almacen almacen = (Almacen) o;
		return getId() != null && Objects.equals(getId(), almacen.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}