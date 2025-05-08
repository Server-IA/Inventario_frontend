package com.coagronet.departamento;

import com.coagronet.estado.Estado;
import com.coagronet.pais.Pais;

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
@Table(name = "departamento", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "depSeqGen")
    @SequenceGenerator(name = "depSeqGen", sequenceName = "departamento_dep_id_seq", allocationSize = 1)
    @Column(name = "dep_id")
    private Integer id;

    @Column(name = "dep_nombre", length = 25, nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dep_pais_id", referencedColumnName = "pai_id", nullable = false)
    private Pais pais;

    @Column(name = "dep_codigo", nullable = false)
    private Integer codigo;

    @Column(name = "dep_acronimo", length = 3, nullable = false)
    private String acronimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dep_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

}
