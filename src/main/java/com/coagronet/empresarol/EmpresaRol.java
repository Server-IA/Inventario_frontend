/*=============================================================================
 Nombre del archivo : EmpresaRol.java
 Descripcion        : Entidad de persistencia para la asignación de roles a
                      empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-06-24 | 0.4.0   | JUAN JOSE CASTRO     | Reemplazo del tipo de dato  |
 |            |         |                      | OffsetDateTime por Instant  |
 |            |         |                      | en los atributos de         |
 |            |         |                      | auditoría createdAt y       |
 |            |         |                      | updatedAt. Cambio de tipo   |
 |            |         |                      | de String a entidad User    |
 |            |         |                      | para createdBy y updatedBy. |
 +------------+---------+----------------------+-----------------------------+
=============================================================================*/

package com.coagronet.empresarol;

import java.time.Instant;

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
@Table(name = "empresa_rol", schema = "public", uniqueConstraints = {
        @UniqueConstraint(name = "uq_empresa_rol_empresa_id_rol_id", columnNames = { "empresa_id", "rol_id" })
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaRol {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empresa_rol_seq")
    @SequenceGenerator(name = "empresa_rol_seq", sequenceName = "empresa_rol_emr_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_empresa_rol_empresa"))
    private Empresa empresa;

    @TenantId
    @Column(name = "empresa_id", insertable = false, updatable = false)
    private Long tenantEmpresaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rol_id", nullable = false, foreignKey = @ForeignKey(name = "fk_empresa_rol_rol"))
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estado_id", nullable = false, foreignKey = @ForeignKey(name = "empresa_rol_estado_id_fkey"))
    private Estado estado;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private Instant updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 150)
    private User createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 150)
    private User updatedBy;

}
