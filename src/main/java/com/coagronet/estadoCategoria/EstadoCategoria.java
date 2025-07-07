package com.coagronet.estadoCategoria;

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
@Table(name = "estado_categoria")
public class EstadoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_categoria_seq")
    @SequenceGenerator(name = "estado_categoria_seq", sequenceName = "estado_categoria_esc_id_seq", allocationSize = 1)
    @Column(name = "esc_id", nullable = false)
    private Long id;


    @Column(name = "esc_nombre")
    private String nombre;

    @Column(name = "esc_descripcion")
    private String descripcion;
}
