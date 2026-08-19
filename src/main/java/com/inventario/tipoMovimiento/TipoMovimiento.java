package com.inventario.tipoMovimiento;

import java.util.Objects;

import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.movimiento.Movimiento;

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
@Table(name = "tipo_movimiento")
public class TipoMovimiento {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_movimiento_generator")
	@SequenceGenerator(name = "tipo_movimiento_generator", sequenceName = "tipo_movimiento_tim_id_seq",
			allocationSize = 1)
	@Column(name = "tim_id", updatable = false, nullable = false)
	private Long id;

	@Column(name = "tim_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "tim_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tim_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tim_empresa_id", referencedColumnName = "emp_id", insertable = false, updatable = false)
	private Empresa empresa;

	@TenantId
	@Column(name = "tim_empresa_id")
	private Long tenantEmpresaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tim_movimiento_id", referencedColumnName = "mov_id", nullable = false)
	private Movimiento movimiento;

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
		TipoMovimiento that = (TipoMovimiento) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}