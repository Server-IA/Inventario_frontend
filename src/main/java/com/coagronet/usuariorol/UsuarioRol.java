/*=============================================================================
 Nombre del archivo : UsuarioRol.java
 Descripcion        : Entidad de persistencia para la asignación de roles a
                      usuarios.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-22 | 0.4.0   | JUAN JOSE CASTRO     | Reemplazo del tipo de dato  |
 |            |         |                      | OffsetDateTime por Instant  |
 |            |         |                      | en los atributos base de    |
 |            |         |                      | auditoría (createdAt,       |
 |            |         |                      | updatedAt y deletedAt).     |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.usuariorol;

import java.io.Serializable;
import java.time.Instant;
import java.time.OffsetDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.TenantId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.rol.Rol;
import com.coagronet.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@DynamicInsert
@EntityListeners(AuditingEntityListener.class)
@Table(name = "usuario_rol", uniqueConstraints = {
		@UniqueConstraint(name = "ux_usuario_empresa_rol", columnNames = { "usuario_id", "empresa_id", "rol_id" })
})
public class UsuarioRol implements Serializable {

	private static final long serialVersionUID = -1706389808605756133L;

	// ===== PK =====
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ===== Relaciones principales =====

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = false, updatable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "rol_id", nullable = false)
	private Rol rol;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false, insertable = false, updatable = false)
	private Empresa empresa;

	@TenantId
	@Column(name = "empresa_id", nullable = false)
	private Long tenantEmpresaId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_id", nullable = false)
	private Estado estado;

	// ===== Campos de contrato =====

	@Column(name = "inicia_contrato_en", nullable = false)
	private OffsetDateTime iniciaContratoEn;

	@Column(name = "finaliza_contrato_en")
	private OffsetDateTime finalizaContratoEn;

	// ===== Auditoría =====

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private Instant updatedAt;

	@Column(name = "deleted_at")
	private Instant deletedAt;

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", updatable = false)
	private User createdBy;

	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by")
	private User updatedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by")
	private User deletedBy;

	// ===== Datos de request =====

	@Column(name = "request_host", length = 255)
	private String requestHost;

	@Column(name = "request_ip", length = 64)
	private String requestIp;

	// ===== Helpers =====

	public String getNombre() {
		return rol != null ? rol.getNombre() : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof UsuarioRol))
			return false;
		UsuarioRol that = (UsuarioRol) o;
		return id != null && id.equals(that.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}