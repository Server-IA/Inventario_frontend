/*=============================================================================
 Nombre del archivo : ArticuloKardex.java
 Descripcion        : Entidad de persistencia para el detalle de Kardex.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-21 | 0.4.0   | JUAN JOSE CASTRO     | Cambio de LocalDateTime a   |
 |            |         |                      | Instant. Eliminación de     |
 |            |         |                      | anotaciones de auditoría de |
 |            |         |                      | modificación. Formateo de   |
 |            |         |                      | anotaciones JPA (@Table,    |
 |            |         |                      | @JoinColumn).               |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.inventario.articuloKardex;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.kardex.Kardex;
import com.inventario.presentacionProducto.PresentacionProducto;
import com.inventario.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "kardex_item", schema = "public", indexes = {
		@Index(name = "idx_kai_empresa_id", columnList = "kai_empresa_id"),
		@Index(name = "idx_kai_kardex_id", columnList = "kai_kardex_id"),
		@Index(name = "idx_kai_producto_id", columnList = "kai_producto_presentacion_id"),
		@Index(name = "idx_kai_responsable_id", columnList = "kai_responsable_id") }, uniqueConstraints = {
				@UniqueConstraint(name = "kardex_item_kai_producto_identificador_key", columnNames = "kai_producto_identificador") })
@EntityListeners(AuditingEntityListener.class)
public class ArticuloKardex {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kai_id", nullable = false, updatable = false)
	private Long id;

	@Column(name = "kai_cantidad", nullable = false, precision = 16, scale = 4)
	private BigDecimal cantidad;

	@Column(name = "kai_precio", nullable = false, precision = 16, scale = 4)
	private BigDecimal precio;

	@Generated
	@Column(name = "kai_precio_total", insertable = false, updatable = false, precision = 16, scale = 4)
	private BigDecimal precioTotal;

	@Column(name = "kai_fecha_vencimiento")
	private LocalDate fechaVencimiento;

	@Column(name = "kai_producto_identificador", columnDefinition = "TEXT")
	private String identificadorProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_kardex_id", referencedColumnName = "kar_id", nullable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_kardex_id_fkey"))
	private Kardex kardex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_producto_presentacion_id_fkey"))
	private PresentacionProducto presentacionProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_estado_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_empresa_id", referencedColumnName = "emp_id", nullable = false, insertable = false, updatable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_empresa_id_fkey"))
	private Empresa empresa;

	@TenantId
	@Column(name = "kai_empresa_id")
	private Long tenantEmpresaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_responsable_id", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_kai_responsable"))
	private User responsable;

	@Column(name = "kai_lote", columnDefinition = "TEXT")
	private String lote;

	// --- Metadatos de Auditoría Integrados con Spring Data ---

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_created_by", referencedColumnName = "usu_id", updatable = false, foreignKey = @ForeignKey(name = "fk_kai_created_by"))
	private User createdBy;

	@Column(name = "kai_seg_rol", length = 100, nullable = false)
	private String rol;

	@JdbcTypeCode(SqlTypes.INET)
	@Column(name = "kai_seg_ip", columnDefinition = "inet", nullable = false)
	private String ip;

	@Column(name = "kai_seg_host", length = 255)
	private String host;

	@CreatedDate
	@Column(name = "kai_created_date", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
	private Instant createdDate;

	@PrePersist
	public void prePersist() {
		if (identificadorProducto == null || identificadorProducto.isBlank()) {
			this.identificadorProducto = UUID.randomUUID().toString();
		}
	}

	@Override
	public final boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass)
			return false;
		ArticuloKardex that = (ArticuloKardex) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}
}