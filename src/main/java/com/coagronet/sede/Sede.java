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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sede", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sedSeqGen")
    @SequenceGenerator(name = "sedSeqGen", sequenceName = "sede_sed_id_seq", allocationSize = 1)
    @Column(name = "sed_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_grupo_id", referencedColumnName = "gru_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_tipo_sede_id", referencedColumnName = "tis_id", nullable = false)
    private TipoSede tipoSede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;

    @Column(name = "sed_nombre", length = 100, nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_municipio_id", referencedColumnName = "mun_id", nullable = false)
    private Municipio municipio;

    @Column(name = "sed_area", nullable = false)
    private Double area;

    @Column(name = "sed_comuna", length = 100, nullable = false)
    private String comuna;

    @Column(name = "sed_descripcion", length = 255, nullable = false)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sed_estado", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @Column(name = "sed_geolocalizacion", length = 255, nullable = false)
    private String geolocalizacion;

    @Column(name = "sed_coordenadas", columnDefinition = "text", nullable = false)
    private String coordenadas;

    @Column(name = "sed_direccion", length = 255, nullable = false)
    private String direccion;
}
