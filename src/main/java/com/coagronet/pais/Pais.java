package com.coagronet.pais;

import java.time.OffsetDateTime;

import com.coagronet.estado.Estado;
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

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "pais", schema = "public", uniqueConstraints = {
		@UniqueConstraint(name = "uq_pais_nombre", columnNames = "pai_nombre"),
		@UniqueConstraint(name = "uq_pais_codigo", columnNames = "pai_codigo"),
		@UniqueConstraint(name = "uq_pais_acronimo", columnNames = "pai_acronimo")
})
public class Pais {

	@Id
	@SequenceGenerator(name = "pai_generator", sequenceName = "pais_pai_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pai_generator")
	@Column(name = "pai_id")
	private Long id;

	@Column(name = "pai_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "pai_codigo", nullable = false)
	private Long codigo;

	@Column(name = "pai_acronimo", length = 3, nullable = false)
	private String acronimo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pai_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "pais_pai_estado_id_fkey"))
	private Estado estado;

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private OffsetDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private OffsetDateTime updatedAt;

	@CreatedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", referencedColumnName = "usu_id", updatable = false, foreignKey = @ForeignKey(name = "fk_pais_created_by"))
	private User createdBy;

	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_pais_updated_by"))
	private User updatedBy;

}
