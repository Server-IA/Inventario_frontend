package com.coagronet.tipoMedicion;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.produccion.Produccion;
import com.coagronet.unidad.Unidad;

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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipo_medicion", schema = "iot")
public class TipoMedicion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipoMedicion_generator")
    @SequenceGenerator(name = "tipoMedicion_generator", sequenceName = "iot.tipo_medicion_tim_id_seq", allocationSize = 1)
    @Column(name="tim_id", nullable = false)
    private Long id;

    @Column(name="tim_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "tim_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "tim_unidad_id", referencedColumnName = "uni_id",nullable = false)
    private Unidad unidad;

    @ManyToOne
    @JoinColumn(name = "tim_produccion_id", referencedColumnName = "pro_id",nullable = false)
    private Produccion produccion;

    @ManyToOne
    @JoinColumn(name = "tim_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "tim_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;
    
}
