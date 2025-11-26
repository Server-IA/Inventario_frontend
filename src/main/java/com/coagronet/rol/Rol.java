package com.coagronet.rol;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.coagronet.estado.Estado;
import com.coagronet.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "rol", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "estado", "createdBy", "updatedBy", "deletedBy" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rol implements Serializable {

	private static final long serialVersionUID = 2L;

	@Id
	@Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "nombre", nullable = false, length = 100, unique = true)
	private String nombre;

	@Column(name = "descripcion", length = 2048)
	private String descripcion;

	// ===== Relaciones =====

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rol_estado"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_rol_created_by"))
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_rol_updated_by"))
	private User updatedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_rol_deleted_by"))
	private User deletedBy;

	// ===== Auditoría de fechas =====

	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@Column(name = "deleted_at")
	private OffsetDateTime deletedAt;
}
