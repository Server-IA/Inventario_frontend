package com.coagronet.departamento;

import java.time.OffsetDateTime;

import com.coagronet.estado.Estado;
import com.coagronet.pais.Pais;
import com.coagronet.user.User;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "departamento", schema = "public", uniqueConstraints = {
		@UniqueConstraint(name = "uq_departamento_pais_nombre", columnNames = { "dep_pais_id", "dep_nombre" }),
		@UniqueConstraint(name = "uq_departamento_pais_codigo", columnNames = { "dep_pais_id", "dep_codigo" }),
		@UniqueConstraint(name = "uq_departamento_pais_acronimo", columnNames = { "dep_pais_id", "dep_acronimo" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departamento {

	@Id
	@SequenceGenerator(name = "departamento_generator", sequenceName = "departamento_dep_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departamento_generator")
	@Column(name = "dep_id")
	private Long id;

	@Column(name = "dep_nombre", length = 70, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dep_pais_id", referencedColumnName = "pai_id", nullable = false,
			foreignKey = @ForeignKey(name = "departamento_dep_pais_id_fkey"))
	private Pais pais;

	@Column(name = "dep_codigo", nullable = false)
	private Integer codigo;

	@Column(name = "dep_acronimo", length = 3, nullable = false)
	private String acronimo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dep_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "departamento_dep_estado_id_fkey"))
	private Estado estado;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", referencedColumnName = "usu_id", updatable = false,
			foreignKey = @ForeignKey(name = "fk_departamento_created_by"))
	private User createdBy;

	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", referencedColumnName = "usu_id",
			foreignKey = @ForeignKey(name = "fk_departamento_updated_by"))
	private User updatedBy;

}
