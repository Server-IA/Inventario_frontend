package com.coagronet.rol;

import java.io.Serializable;
import java.time.Instant;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.coagronet.estado.Estado;
import com.coagronet.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@EntityListeners(AuditingEntityListener.class)
@DynamicInsert
@DynamicUpdate
@SQLDelete(sql = "UPDATE public.rol SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Rol implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Long id;

	@NaturalId
	@EqualsAndHashCode.Include
	@Column(name = "nombre", nullable = false, length = 100, unique = true)
	private String nombre;

	@Column(name = "descripcion", length = 2048)
	private String descripcion;

	// ===== Relaciones =====

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rol_estado"))
	private Estado estado;

	// ===== Auditoría Automatizada =====

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", referencedColumnName = "usu_id", updatable = false, foreignKey = @ForeignKey(name = "fk_rol_created_by"))
	private User createdBy;

	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_rol_updated_by"))
	private User updatedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_rol_deleted_by"))
	private User deletedBy;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;
}