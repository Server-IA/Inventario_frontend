package com.inventario.cierreinventariodetalle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import org.hibernate.proxy.HibernateProxy;

import com.inventario.almacen.Almacen;
import com.inventario.cierreinventario.CierreInventario;
import com.inventario.empresa.Empresa;
import com.inventario.presentacionProducto.PresentacionProducto;

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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cierre_inventario_detalle", schema = "public", indexes = {
		@Index(name = "idx_cierre_detalle_lookup", columnList = "cid_fecha_corte, cid_empresa_id, cid_almacen_id") })
public class CierreInventarioDetalle {

	@Id
	@SequenceGenerator(name = "cierre_inventario_detalle_generator",
			sequenceName = "cierre_inventario_detalle_cid_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cierre_inventario_detalle_generator")
	@Column(name = "cid_id", nullable = false, updatable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cid_cierre_inventario_id", referencedColumnName = "cii_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_detalle_cid_cierre_inventario_id_fkey"))
	private CierreInventario cierreInventario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cid_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_detalle_cid_producto_presentacion_id_fkey"))
	private PresentacionProducto presentacionProducto;

	@Column(name = "cid_stock", nullable = false, precision = 20, scale = 6)
	private BigDecimal stock;

	@Column(name = "cid_fecha_corte", nullable = false)
	private LocalDate fechaCorte;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cid_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_detalle_cid_empresa_id_fkey"))
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cid_almacen_id", referencedColumnName = "alm_id", nullable = false,
			foreignKey = @ForeignKey(name = "cierre_inventario_detalle_cid_almacen_id_fkey"))
	private Almacen almacen;

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
		CierreInventarioDetalle that = (CierreInventarioDetalle) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}