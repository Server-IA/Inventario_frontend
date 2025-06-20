package com.coagronet.inventario;

import com.coagronet.empresa.Empresa;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @Column(name = "inv_id", nullable = false)
    private Long id;

    @Column(name = "inv_nombre")
    private String nombre;

    @Column(name = "inv_descripcion")
    private String descripcion;

    @Column(name = "inv_fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "inv_tipo_inventario_id")
    private Long tipoInventarioId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inv_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @Column(name = "inv_subseccion_id")
    private Long subseccionId;

    @Column(name = "inv_estado_id")
    private Long estadoId;
}
