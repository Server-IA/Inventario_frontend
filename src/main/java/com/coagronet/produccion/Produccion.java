package com.coagronet.produccion;

import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
import com.coagronet.estado.Estado;
import com.coagronet.producto.Producto;
import com.coagronet.tipoProduccion.TipoProduccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "produccion")
public class Produccion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produccion_generator")
    @SequenceGenerator(name = "produccion_generator", sequenceName = "produccion_pro_id_seq", allocationSize = 1)
    @Column(name = "pro_id", nullable = false)
    private Long id;

    @Column(name = "pro_nombre", length = 100)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "pro_tipo_produccion_id", referencedColumnName = "tip_id")
    private TipoProduccion tipoProduccion;

    @Column(name = "pro_descripcion", length = 255)
    private String descripcion;

    @Column(name = "pro_fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "pro_fecha_final")
    private LocalDateTime fechaFinal;

    @ManyToOne
    @JoinColumn(name = "pro_espacio_id", referencedColumnName = "esp_id")
    private Espacio espacio;

    @ManyToOne
    @JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "pro_producto_id", referencedColumnName = "pro_id")
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

}
