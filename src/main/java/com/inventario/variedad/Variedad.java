package com.inventario.variedad;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.tipoProduccion.TipoProduccion;

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
@Table(name="variedad", schema="iot")
public class Variedad {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="variedad_generator")
    @SequenceGenerator(name="variedad_generator", sequenceName="iot.variedad_var_id_seq", allocationSize = 1)
    @Column(name="var_id", nullable=false)
    private Long id;

    @Column(name="var_nombre", length=100, nullable=false)
    private String nombre;

    @Column(name="var_descripcion", length=2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="var_tipo_produccion_id", referencedColumnName= "tip_id", nullable=false)
    private TipoProduccion tipoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="var_estado_id", referencedColumnName="est_id", nullable=false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="var_empresa_id", referencedColumnName="emp_id", nullable=false)
    private Empresa empresa;


    
}
