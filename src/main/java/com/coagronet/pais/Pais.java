package com.coagronet.pais;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pais", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paiSeqGen")
    @SequenceGenerator(name = "paiSeqGen", sequenceName = "pais_pai_id_seq", allocationSize = 1)
    @Column(name = "pai_id")
    private Integer id;

    @Column(name = "pai_nombre", length = 25, nullable = false)
    private String nombre;

    @Column(name = "pai_codigo", nullable = false)
    private Integer codigo;

    @Column(name = "pai_acronimo", length = 3, nullable = false)
    private String acronimo;

    @Column(name = "pai_propietario", nullable = false)
    private Integer propietario;
}

