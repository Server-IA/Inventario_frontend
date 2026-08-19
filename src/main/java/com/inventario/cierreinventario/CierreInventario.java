package com.inventario.cierreinventario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.Generated;
import org.hibernate.proxy.HibernateProxy;

import com.inventario.almacen.Almacen;
import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.user.User;

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

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cierre_inventario", schema = "public", uniqueConstraints = { @UniqueConstraint(name = "ux_cierre_unico",
		columnNames = { "cii_empresa_id", "cii_almacen_id", "cii_fecha_corte" }) })
public class CierreInventario {

	@Id
	@SequenceGenerator(name = "cierre_inventario_generator", sequenceName = "cierre_inventario_cii_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cierre_inventario_generator")
	@Column(name = "cii_id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "cii_fecha_corte", nullable = false)
	private LocalDate fechaCorte;

	@Column(name = "cii_fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cii_almacen_id", referencedColumnName = "alm_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_cii_almacen_id_fkey"))
	private Almacen almacen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cii_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_cii_empresa_id_fkey"))
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cii_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_cii_estado_id_fkey"))
	private Estado estado;

	@Generated
	@Column(name = "cii_fecha_creacion", insertable = false, updatable = false, nullable = false)
	private LocalDateTime fechaCreacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cii_usuario_id", referencedColumnName = "usu_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_cii_usuario_id_fkey"))
	private User usuario;

	@Column(name = "cii_descripcion", length = 1048)
	private String descripcion;

	// --- Resolución segura de Proxies de Spring Data JPA ---

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
		CierreInventario that = (CierreInventario) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}