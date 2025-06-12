package com.coagronet.ocupacion;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.evaluacion.Evaluacion;
import com.coagronet.tipoActividad.TipoActividad;

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
@Table(name = "actividad_ocupacion", schema = "public")
public class Ocupacion {

    @Id
    @SequenceGenerator(name = "actividad_ocupacion_generator", sequenceName = "actividad_ocupacion_aco_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actividad_ocupacion_generator")
    @Column(name = "aco_id")
    private Long id;

    @Column(name = "aco_nombre", length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aco_tipo_actividad_id", referencedColumnName = "tia_id", nullable = false, foreignKey = @ForeignKey(name = "actividad_ocupaciontipo_actividadtia_id_fkey"))
    private TipoActividad tipoActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aco_evaluacion_id", referencedColumnName = "eva_id", nullable = false, foreignKey = @ForeignKey(name = "actividad_ocupacion_aco_evaluacion_id_fkey"))
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aco_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "actividad_ocupacion_aco_estado_id_fkey"))
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aco_empresa_id", referencedColumnName = "emp_id", nullable = false, foreignKey = @ForeignKey(name = "actividad_ocupacion_aco_empresa_id_fkey"))
    private Empresa empresa;

}
