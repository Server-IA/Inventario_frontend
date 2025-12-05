package com.coagronet.empresarol;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @Column(name = "created_at", insertable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;


}
