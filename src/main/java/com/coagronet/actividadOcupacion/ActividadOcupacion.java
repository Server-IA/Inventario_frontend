package com.coagronet.actividadOcupacion;

import com.coagronet.evaluacion.Evaluacion;
import com.coagronet.tipoActividad.TipoActividad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "actividad_ocupacion")
public class ActividadOcupacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actividad_ocupacion_generator")
    @SequenceGenerator(name = "actividad_ocupacion_generator", sequenceName = "actividad_ocupacion_aco_id_seq", allocationSize = 1)
    @Column(name = "aco_id", nullable = false)
    private Long id;

    @Column(name = "aco_nombre", length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aco_tipo_actividad_id", referencedColumnName = "tia_id")
    private TipoActividad tipoActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aco_evaluacion_id", referencedColumnName = "eva_id")
    private Evaluacion evaluacion;

}
