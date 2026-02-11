package com.coagronet.produccionDesarrollo;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.fase.Fase;
import com.coagronet.tipoMedicion.TipoMedicion;
import com.coagronet.variedad.Variedad;

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

@Data @Builder @Entity @NoArgsConstructor @AllArgsConstructor @Table(name = "produccion_desarrollo", schema = "iot")
public class ProduccionDesarrollo {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produccionDesarrollo_generator") @SequenceGenerator(name = "produccionDesarrollo_generator", sequenceName = "iot.produccion_desarrollo_prd_id_seq", allocationSize = 1) @Column(name = "prd_id", nullable = false)
    private Long id;

    @Column(name = "prd_descripcion", length = 2048)
    private String descripcion;

    @Column(name = "prd_valor", nullable = false)
    private Double valor;

    @ManyToOne @JoinColumn(name = "prd_variedad_id", referencedColumnName = "var_id", nullable = false)
    private Variedad variedad;

    @ManyToOne @JoinColumn(name = "prd_fase_id", referencedColumnName = "fas_id", nullable = false)
    private Fase fase;

    @ManyToOne @JoinColumn(name = "prd_tipo_medicion_id", referencedColumnName = "tim_id", nullable = false)
    private TipoMedicion tipoMedicion;

    @ManyToOne @JoinColumn(name = "prd_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne @JoinColumn(name = "prd_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;

}
