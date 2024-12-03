package com.coagronet.categoriaActividad;

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
@Table(name = "categoria_actividad")
public class CategoriaActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categoria_actividad_generator")
    @SequenceGenerator(name = "categoria_actividad_generator", sequenceName = "categoria_actividad_caa_id_seq", allocationSize = 1)
    @Column(name = "caa_id", nullable = false)
    private Integer id;

    @Column(name = "caa_nombre", length = 100)
    private String nombre;

    @Column(name = "caa_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caa_estado", referencedColumnName = "est_id")
    private Estado estado;

}
