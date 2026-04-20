package com.coagronet.productopresentacionstock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.presentacionProducto.PresentacionProducto;

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
@Table(name = "producto_presentacion_stock", schema = "public",
		indexes = { @Index(name = "idx_pps_almacen_id", columnList = "pps_almacen_id") },
		uniqueConstraints = { @UniqueConstraint(name = "uq_pps_presentacion_almacen_empresa",
				columnNames = { "pps_producto_presentacion_id", "pps_almacen_id", "pps_empresa_id" }) })
public class ProductoPresentacionStock {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_presentacion_stock_generator")
	@SequenceGenerator(name = "producto_presentacion_stock_generator",
			sequenceName = "producto_presentacion_stock_pps_id_seq", allocationSize = 1)
	@Column(name = "pps_id", nullable = false, updatable = false)
	private Long id;

	@Builder.Default
	@Column(name = "pps_stock", nullable = false, precision = 20, scale = 6)
	private BigDecimal stock = BigDecimal.ZERO;

	@Builder.Default
	@Column(name = "pps_fecha_hora", nullable = false)
	private LocalDateTime fechaHora = LocalDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pps_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false,
			foreignKey = @ForeignKey(name = "fk_pps_producto_presentacion"))
	private PresentacionProducto productoPresentacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pps_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "producto_presentacion_stock_pps_empresa_id_fkey"))
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pps_almacen_id", referencedColumnName = "alm_id",
			foreignKey = @ForeignKey(name = "fk_pps_almacen"))
	private Almacen almacen;

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
		ProductoPresentacionStock that = (ProductoPresentacionStock) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}