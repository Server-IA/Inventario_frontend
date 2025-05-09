package com.coagronet.pais;

import com.coagronet.empresa.Empresa;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@Entity
@Table(name = "pais", schema = "public")
public class Pais {

    @Id
    @SequenceGenerator(name = "pai_generator", sequenceName = "pais_pai_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pai_generator")
    @Column(name = "pai_id")
    private Long id;

    @Column(name = "pai_nombre", length = 70, nullable = false)
    private String nombre;

    @Column(name = "pai_codigo", nullable = false)
    private Long codigo;

    @Column(name = "pai_acronimo", length = 3, nullable = false)
    private String acronimo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

}
