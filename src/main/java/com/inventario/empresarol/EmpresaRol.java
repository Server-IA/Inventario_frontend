/*=============================================================================
 Nombre del archivo : EmpresaRol.java
 Descripcion        : Entidad de persistencia para la asignación de roles a
                      empresas.
===============================================================================
 CONTROL DE CAMBIOS
 +------------+---------+----------------------+-----------------------------+
 |    Fecha   | Versión |       Autor          | Descripción del cambio      |
 +------------+---------+----------------------+-----------------------------+
 | 2026-08-12 | 0.4.1   | Oscar Andrade        | Fix cross-tenant y auditoría|
 |            |         |                      | - Eliminado @TenantId para  |
 |            |         |                      |   permitir consulta de roles|
 |            |         |                      |   de otras empresas.        |
 |            |         |                      | - createdBy/updatedBy de    |
 |            |         |                      |   @Column a @ManyToOne.     |
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

package com.inventario.empresarol;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.rol.Rol;
import com.inventario.user.User;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "usu_id", updatable = false, foreignKey = @ForeignKey(name = "fk_empresa_rol_created_by"))
    private User createdBy;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", referencedColumnName = "usu_id", foreignKey = @ForeignKey(name = "fk_empresa_rol_updated_by"))
    private User updatedBy;

}
