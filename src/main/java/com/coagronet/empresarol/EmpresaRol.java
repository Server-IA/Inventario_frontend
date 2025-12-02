package com.coagronet.empresarol;

import com.coagronet.empresa.Empresa;
import com.coagronet.rol.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(schema = "public", name = "empresa_rol")
public class EmpresaRol {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "empresa_rol_generator")
    @SequenceGenerator(name = "empresa_rol_generator", sequenceName = "empresa_rol_emr_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    private Rol rol;

    @Column(name = "estado_id")
    private String estado;

    @Column(name = "created_at", insertable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
