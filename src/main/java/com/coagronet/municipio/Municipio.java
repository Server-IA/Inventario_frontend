/*=============================================================================
 Nombre del archivo : Municipio.java
 Descripcion        : Entidad JPA para el catalogo global de municipios.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 |   Fecha    | Version |      Autor           | Descripcion del cambio                                                                                                   |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
 | 2024-10-21 | 1.0.0   | jujcgu               | Creacion del archivo.                                                                                                              |
 | 2026-05-27 | 1.1.0   | JUAN DIAZ            | Refactor de catalogos globales: ajustes en entidades, DTOs, mappers, repositorios y servicios, con validaciones de negocio.        |
 +------------+---------+----------------------+------------------------------------------------------------------------------------------------------------------------------------+
=============================================================================*/
package com.coagronet.municipio;

import java.time.OffsetDateTime;

import com.coagronet.departamento.Departamento;
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
@Table(name = "municipio", schema = "public", uniqueConstraints = {
		@UniqueConstraint(name = "uq_municipio_departamento_nombre", columnNames = { "mun_departamento_id",
				"mun_nombre" }),
		@UniqueConstraint(name = "uq_municipio_departamento_codigo", columnNames = { "mun_departamento_id",
				"mun_codigo" }),
		@UniqueConstraint(name = "uq_municipio_departamento_acronimo", columnNames = { "mun_departamento_id",
				"mun_acronimo" })
})
public class Municipio {

	@Id
	@SequenceGenerator(name = "municipio_generator", sequenceName = "municipio_mun_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipio_generator")
	@Column(name = "mun_id")
	private Long id;

	@Column(name = "mun_nombre", length = 60, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mun_departamento_id", referencedColumnName = "dep_id", nullable = false,
			foreignKey = @ForeignKey(name = "municipio_mun_departamento_id_fkey"))
	private Departamento departamento;

	@Column(name = "mun_codigo")
	private Integer codigo;

	@Column(name = "mun_acronimo", length = 3)
	private String acronimo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mun_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "municipio_mun_estado_id_fkey"))
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
			foreignKey = @ForeignKey(name = "fk_municipio_created_by"))
	private User createdBy;

	@LastModifiedBy
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", referencedColumnName = "usu_id",
			foreignKey = @ForeignKey(name = "fk_municipio_updated_by"))
	private User updatedBy;

}









