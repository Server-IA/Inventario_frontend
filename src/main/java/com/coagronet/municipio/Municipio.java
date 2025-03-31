package com.coagronet.municipio;

import com.coagronet.departamento.Departamento;

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
@Table(name = "municipio", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "munSeqGen")
    @SequenceGenerator(name = "munSeqGen", sequenceName = "municipio_mun_id_seq", allocationSize = 1)
    @Column(name = "mun_id")
    private Integer id;

    @Column(name = "mun_nombre", length = 25, nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mun_departamento_id", referencedColumnName = "dep_id", nullable = false)
    private Departamento departamento;

    @Column(name = "mun_codigo", nullable = false)
    private Integer codigo;

    @Column(name = "mun_acronimo", length = 3, nullable = false)
    private String acronimo;
}
