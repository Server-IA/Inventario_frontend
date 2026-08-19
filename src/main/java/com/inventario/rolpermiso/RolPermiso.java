package com.inventario.rolpermiso;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inventario.empresarol.EmpresaRol;
import com.inventario.estado.Estado;
import com.inventario.permiso.Permiso;
import com.inventario.user.User;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rol_permiso", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RolPermiso {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_rol_id", referencedColumnName = "id")
	private EmpresaRol empresaRol;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "permiso_id", referencedColumnName = "id")
	private Permiso permiso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Instant createdAt;

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", referencedColumnName = "usu_id", updatable = false)
	private User createdBy;

	@Column(name = "deleted_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private Instant deletedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "deleted_by", referencedColumnName = "usu_id")
	private User deletedBy;

}
