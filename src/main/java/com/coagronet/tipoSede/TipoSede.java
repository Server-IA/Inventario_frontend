package com.coagronet.tipoSede;

import com.coagronet.estado.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "tipo_sede")
public class TipoSede {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_sede_generator")
    @SequenceGenerator(name = "tipo_sede_generator", sequenceName = "tipo_sede_tis_id_seq", allocationSize = 1)
    @Column(name = "tis_id", nullable = false)
    private Integer id;

    @Column(name = "tis_nombre")
    private String nombre;

    @Column(name = "tis_descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "tis_estado", referencedColumnName = "est_id")
    private Estado estado;
}
