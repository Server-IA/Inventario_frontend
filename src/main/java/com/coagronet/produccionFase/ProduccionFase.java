package com.coagronet.produccionFase;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.fase.Fase;
import com.coagronet.tipoProduccion.TipoProduccion;
import com.coagronet.variedad.Variedad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProduccionFase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "productoFase_generator")
    @SequenceGenerator(name = "productoFase_generator", sequenceName = "iot.producto_fase_prf_id_seq", allocationSize = 1)
    @Column(name = "prf_id", nullable = false)
    private Long id;

    @Column(name = "prf_dias", nullable = false)
    private Integer dias;

    @Column(name = "prf_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prf_variedad_id", referencedColumnName = "var_id", nullable = false)
    private Variedad variedad;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prf_tipo_produccion_id", referencedColumnName = "tip_id", nullable = false)
    private TipoProduccion tipoProduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prf_fase_id", referencedColumnName = "fas_id", nullable = false)
    private Fase fase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prf_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prd_empresa_id", referencedColumnName = "emp_id", nullable= false)
    private Empresa empresa;
    
}
