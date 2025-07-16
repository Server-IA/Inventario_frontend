package com.coagronet.tipoFase;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tipo_fase")
public class TipoFase{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipoFase_generator")
    @SequenceGenerator(name="tipoFase_generator", sequenceName = "iot.tipo_fase_tif_id_seq", allocationSize = 1)
    @Column(name="tif_id", nullable = false)
    private Long id;

    @Column(name = "tif_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name="tif_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tif_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tif_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;
    
}