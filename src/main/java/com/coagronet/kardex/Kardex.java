package com.coagronet.kardex;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TenantId;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.ordenCompra.OrdenCompra;
import com.coagronet.pedido.Pedido;
import com.coagronet.produccion.Produccion;
import com.coagronet.tipoMovimiento.TipoMovimiento;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "kardex",
		indexes = { @Index(name = "idx_kardex_almacen_id", columnList = "kar_almacen_id"),
				@Index(name = "idx_kardex_almacen_destino", columnList = "kar_almacen_destino_id"),
				@Index(name = "idx_kardex_empresa_id", columnList = "kar_empresa_id"),
				@Index(name = "idx_kardex_orden_compra_id", columnList = "kar_orden_compra_id"),
				@Index(name = "idx_kardex_pedido_id", columnList = "kar_pedido_id"),
				@Index(name = "idx_kardex_produccion_id", columnList = "kar_produccion_id") })
@EntityListeners(AuditingEntityListener.class)
public class Kardex {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "kar_id", nullable = false, updatable = false)
	private Long id;

	@Builder.Default
	@Column(name = "kar_fecha_hora", columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false)
	private OffsetDateTime fechaHora = OffsetDateTime.now();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_almacen_id", referencedColumnName = "alm_id", nullable = false)
	private Almacen almacen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_almacen_destino_id", referencedColumnName = "alm_id")
	private Almacen almacenDestino;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_produccion_id", referencedColumnName = "pro_id")
	private Produccion produccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_tipo_movimiento_id", referencedColumnName = "tim_id", nullable = false, updatable = false)
	private TipoMovimiento tipoMovimiento;

	@Column(name = "kar_descripcion", columnDefinition = "TEXT")
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kar_empresa_id", referencedColumnName = "emp_id",nullable = false, insertable = false, updatable = false)
    private Empresa empresa;

	@TenantId 
    @Column(name = "kar_empresa_id")
    private Long tenantEmpresaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_cliente_proveedor_id", referencedColumnName = "emp_id")
	private Empresa clienteProveedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_pedido_id", referencedColumnName = "ped_id")
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_orden_compra_id", referencedColumnName = "orc_id")
	private OrdenCompra ordenCompra;

	// --- Metadatos de Auditoría y Seguridad ---

	@LastModifiedBy
	@Column(name = "kar_seg_username", length = 150)
	private String username;

	@Column(name = "kar_seg_rol", length = 100)
	private String rol;

	@JdbcTypeCode(SqlTypes.INET)
	@Column(name = "kar_seg_ip", columnDefinition = "inet")
	private String ip;

	@Column(name = "kar_seg_host", length = 255)
	private String host;

	@LastModifiedDate
	@CreatedDate
	@Column(name = "kar_seg_fecha_hora", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private LocalDateTime segFechaHora;

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
		Kardex kardex = (Kardex) o;
		return getId() != null && Objects.equals(getId(), kardex.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
				: getClass().hashCode();
	}

}