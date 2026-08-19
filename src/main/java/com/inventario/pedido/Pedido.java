package com.inventario.pedido;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;

import com.inventario.almacen.Almacen;
import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.produccion.Produccion;

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
@Table(name = "pedido", schema = "public",
		indexes = { @Index(name = "idx_ped_empresa_id", columnList = "ped_empresa_id"),
				@Index(name = "idx_ped_almacen_id", columnList = "ped_almacen_id"),
				@Index(name = "idx_ped_produccion_id", columnList = "ped_produccion_id"),
				@Index(name = "idx_ped_estado_id", columnList = "ped_estado_id") })
public class Pedido {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_generator")
	@SequenceGenerator(name = "pedido_generator", sequenceName = "pedido_id_seq", allocationSize = 1)
	@Column(name = "ped_id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "ped_fecha_hora")
	private LocalDateTime fechaHora;

	@Column(name = "ped_descripcion", length = 2048, nullable = false)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ped_almacen_id", referencedColumnName = "alm_id", nullable = false)
	private Almacen almacen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ped_produccion_id", referencedColumnName = "pro_id", nullable = false)
	private Produccion produccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ped_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ped_empresa_id", referencedColumnName = "emp_id", nullable = false, insertable = false,
			updatable = false)
	private Empresa empresa;

	@TenantId
	@Column(name = "ped_empresa_id", nullable = false, updatable = false)
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
		Pedido pedido = (Pedido) o;
		return getId() != null && Objects.equals(getId(), pedido.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}