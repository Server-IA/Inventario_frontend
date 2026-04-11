package com.coagronet.presentacionProducto;

import java.util.Objects;

import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.marca.Marca;
import com.coagronet.presentacion.Presentacion;
import com.coagronet.producto.Producto;
import com.coagronet.unidad.Unidad;

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
@Table(name = "producto_presentacion", schema = "public",
		indexes = { @Index(name = "idx_prp_empresa_id", columnList = "prp_empresa_id"),
				@Index(name = "idx_prp_producto_id", columnList = "prp_producto_id"),
				@Index(name = "idx_prp_estado_id", columnList = "prp_estado_id"),
				@Index(name = "idx_prp_marca_id", columnList = "prp_marca_id"),
				@Index(name = "idx_prp_presentacion_id", columnList = "prp_presentacion_id") })
public class PresentacionProducto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prp_generator")
	@SequenceGenerator(name = "prp_generator", sequenceName = "producto_presentacion_prp_id_seq", allocationSize = 1)
	@Column(name = "prp_id", updatable = false, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prp_producto_id", nullable = false)
	private Producto producto;

	@Column(name = "prp_nombre", length = 100, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prp_unidad_id", nullable = false)
	private Unidad unidad;

	@Column(name = "prp_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prp_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@Column(name = "prp_cantidad", nullable = false)
	private Double cantidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prp_marca_id", nullable = false)
	private Marca marca;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prp_presentacion_id", nullable = false)
	private Presentacion presentacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prp_empresa_id", nullable = false, insertable = false, updatable = false)
	private Empresa empresa;

	@TenantId
	@Column(name = "prp_empresa_id", nullable = false, updatable = false)
	private Long tenantEmpresaId;

	@Column(name = "prp_desgregar")
	private Boolean desgregar;

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
		PresentacionProducto that = (PresentacionProducto) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}