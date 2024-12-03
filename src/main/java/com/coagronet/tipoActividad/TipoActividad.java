package com.coagronet.tipoActividad;

import com.coagronet.categoriaActividad.CategoriaActividad;
import com.coagronet.estado.Estado;
import com.coagronet.proceso.Proceso;

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
@Table(name = "tipo_actividad")
public class TipoActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_actividad_generator")
    @SequenceGenerator(name = "tipo_actividad_generator", sequenceName = "tipo_actividad_tia_id_seq", allocationSize = 1)
    @Column(name = "tia_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tia_categoria_actividad_id", referencedColumnName = "caa_id")
    private CategoriaActividad categoriaActividad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tia_proceso_id", referencedColumnName = "pro_id")
    private Proceso proceso;

    @Column(name = "tia_nombre", length = 100)
    private String nombre;

    @Column(name = "tia_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tia_estado", referencedColumnName = "est_id")
    private Estado estado;

}
