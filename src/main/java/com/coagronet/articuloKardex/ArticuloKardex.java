package com.coagronet.articuloKardex;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.kardex.Kardex;
import com.coagronet.presentacionProducto.PresentacionProducto;
import com.coagronet.user.User;

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
@Table(name = "kardex_item", schema = "public",
		indexes = { @Index(name = "idx_kai_empresa_id", columnList = "kai_empresa_id"),
				@Index(name = "idx_kai_kardex_id", columnList = "kai_kardex_id"),
				@Index(name = "idx_kai_producto_id", columnList = "kai_producto_presentacion_id"),
				@Index(name = "idx_kai_responsable_id", columnList = "kai_responsable_id") // Índice
																							// añadido
		},
		uniqueConstraints = { @UniqueConstraint(name = "kardex_item_kai_producto_identificador_key",
				columnNames = "kai_producto_identificador") })
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

	@Column(name = "kai_precio_total", insertable = false, updatable = false, precision = 16, scale = 4)
	private BigDecimal precioTotal;

	@Column(name = "kai_fecha_vencimiento")
	private LocalDate fechaVencimiento;

	@Column(name = "kai_producto_identificador", columnDefinition = "TEXT")
	private String identificadorProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_kardex_id", referencedColumnName = "kar_id", nullable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_kardex_id_fkey"))
	private Kardex kardex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_producto_presentacion_id_fkey"))
	private PresentacionProducto presentacionProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_estado_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_empresa_id", referencedColumnName = "emp_id", nullable = false, updatable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_empresa_id_fkey"))
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_responsable_id", referencedColumnName = "usu_id",
			foreignKey = @ForeignKey(name = "fk_kai_responsable"))
	private User responsable;

	@Column(name = "kai_lote", columnDefinition = "TEXT")
	private String lote;

	// --- Metadatos de Auditoría Integrados con Spring Data ---

	@CreatedBy
	@Column(name = "kai_seg_username", length = 150, nullable = false, updatable = false)
	private String username;

	@Column(name = "kai_seg_rol", length = 100, nullable = false, updatable = false)
	private String rol;

	@Column(name = "kai_seg_ip", columnDefinition = "inet", nullable = false, updatable = false)
	private String ip;

	@Column(name = "kai_seg_host", length = 255, updatable = false)
	private String host;

	@CreatedDate
	@Column(name = "kai_seg_fecha_hora", columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false,
			updatable = false)
	private OffsetDateTime fechaHora;

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
				? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy
				? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
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