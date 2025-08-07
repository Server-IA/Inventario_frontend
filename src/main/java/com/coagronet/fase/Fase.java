package com.coagronet.fase;


import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.tipoFase.TipoFase;

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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fase", schema = "iot")
public class Fase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fase_generator")
    @SequenceGenerator(name="fase_generator", sequenceName = "iot.fase_fas_id_seq", allocationSize = 1)
    @Column(name = "fas_id", nullable = false)
    private Long id;

    @Column(name="fas_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name="fas_descripcion", length = 2048, nullable = false)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name="fas_tipo_fase_id", referencedColumnName = "tif_id", nullable = false)
    private TipoFase tipoFase;

    @ManyToOne
    @JoinColumn(name="fas_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name="fas_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;





    
}
