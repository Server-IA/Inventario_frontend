package com.inventario.ordenCompra;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.pedido.Pedido;
import com.inventario.proveedor.Proveedor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
@Table(name = "orden_compra", schema = "public",
		indexes = { @Index(name = "idx_orc_empresa_id", columnList = "orc_empresa_id"),
				@Index(name = "idx_orc_pedido_id", columnList = "orc_pedido_id"),
				@Index(name = "idx_orc_estado_id", columnList = "orc_estado_id") })
public class OrdenCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_compra_generator")
	@SequenceGenerator(name = "orden_compra_generator", sequenceName = "orden_compra_orc_id_seq", allocationSize = 1)
	@Column(name = "orc_id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "orc_fecha_hora")
	private LocalDateTime fechaHora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_pedido_id", referencedColumnName = "ped_id", nullable = false,
			foreignKey = @ForeignKey(name = "orden_compra_orc_pedido_id_fkey"))
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_proveedor_id", referencedColumnName = "pro_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "orden_compra_orc_proveedor_id_fkey"))
	private Proveedor proveedor;

	@Column(name = "orc_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "orden_compra_orc_estado_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_empresa_id", referencedColumnName = "emp_id", nullable = false, insertable = false,
			updatable = false, foreignKey = @ForeignKey(name = "orden_compra_orc_empresa_id_fkey"))
	private Empresa empresa;

	@TenantId
	@Column(name = "orc_empresa_id", nullable = false)
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
		OrdenCompra that = (OrdenCompra) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}