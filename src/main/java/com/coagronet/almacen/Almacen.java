package com.coagronet.almacen;

import com.coagronet.espacio.Espacio;
import com.coagronet.estado.Estado;
import com.coagronet.sede.Sede;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "almacen", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "almSeqGen")
    @SequenceGenerator(name = "almSeqGen", sequenceName = "almacen_alm_id_seq", allocationSize = 1)
    @Column(name = "alm_id")
    private Integer id;

    @Column(name = "alm_nombre", length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinColumn(name = "alm_sede_id", referencedColumnName = "sed_id")
    private Sede sede;

    @Column(name = "alm_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alm_estado", referencedColumnName = "est_id")
    private Estado estado;

    @Column(name = "alm_geolocalizacion", length = 255)
    private String geolocalizacion;

    @Column(name = "alm_coordenadas", columnDefinition = "text")
    private String coordenadas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alm_espacio_id", referencedColumnName = "esp_id")
    private Espacio espacio;

    @Column(name = "alm_direccion", length = 255)
    private String direccion;
}

