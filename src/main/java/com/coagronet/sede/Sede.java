package com.coagronet.sede;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.grupo.Grupo;
import com.coagronet.municipio.Municipio;
import com.coagronet.tipoSede.TipoSede;

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
@Table(name = "sede")
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sede_generator")
    @SequenceGenerator(name = "sede_generator", sequenceName = "sede_sed_id_seq", allocationSize = 1)
    @Column(name = "sed_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_grupo_id", referencedColumnName = "gru_id")
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_tipo_sede_id", referencedColumnName = "tis_id")
    private TipoSede tipoSede;

    @ManyToOne
    @JoinColumn(name = "sed_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @Column(name = "sed_nombre", length = 100)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_municipio_id", referencedColumnName = "mun_id")
    private Municipio municipio;

    @Column(name = "sed_geolocalizacion")
    private String geolocalizacion;

    @Column(name = "sed_coordenadas")
    private String coordenadas;

    @Column(name = "sed_area")
    private Double area;

    @Column(name = "sed_comuna", length = 100)
    private String comuna;

    @Column(name = "sed_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_estado", referencedColumnName = "est_id")
    private Estado estado;

}
