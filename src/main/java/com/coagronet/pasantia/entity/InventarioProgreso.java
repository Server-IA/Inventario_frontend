package com.coagronet.pasantia.entity;

import java.time.OffsetDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario_progreso", schema = "pasantia")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventarioProgreso {

    @EmbeddedId
    private InventarioProgresoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("inventarioId")
    @JoinColumn(name = "inventario_id")
    private Inventario inventario;

    @Column(name = "emp_id", nullable = false)
    private Long empId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "emp_id", referencedColumnName = "emp_id", insertable = false, updatable = false),
            @JoinColumn(name = "producto_identificador", referencedColumnName = "identificador", insertable = false, updatable = false)
    })
    private Producto producto;

    @Column(name = "encontrado", nullable = false)
    private Boolean encontrado = false;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "estado", nullable = false, columnDefinition = "pasantia.condicion_item")
    private CondicionItem estado = CondicionItem.ok;

    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @LastModifiedDate
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}