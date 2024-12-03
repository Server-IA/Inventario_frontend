package com.coagronet.evaluacion;

import com.coagronet.estado.Estado;

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
@Table(name = "evaluacion")
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluacion_generator")
    @SequenceGenerator(name = "evaluacion_generator", sequenceName = "evaluacion_eva_id_seq", allocationSize = 1)
    @Column(name = "eva_id", nullable = false)
    private Integer id;

    @Column(name = "eva_nombre", length = 100)
    private String nombre;

    @Column(name = "eva_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eva_estado", referencedColumnName = "est_id")
    private Estado estado;

}
