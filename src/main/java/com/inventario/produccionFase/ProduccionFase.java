package com.inventario.produccionFase;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.fase.Fase;
import com.inventario.tipoProduccion.TipoProduccion;
import com.inventario.variedad.Variedad;

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
@Table(name = "produccion_fase", schema = "iot")
public class ProduccionFase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "productoFase_generator")
    @SequenceGenerator(name = "productoFase_generator", sequenceName = "iot.produccion_fase_prf_id_seq", allocationSize = 1)
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
    @JoinColumn(name = "prf_empresa_id", referencedColumnName = "emp_id", nullable= false)
    private Empresa empresa;
    
}
