package com.inventario.subSeccionDispositivo;



import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.subseccion.Subseccion;
import com.inventario.tipoDispositivo.TipoDispositivo;

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
@Table(name = "sub_seccion_dispositivo", schema = "iot")
public class SubseccionDispositivo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subSeccionDispositivo_generator")
    @SequenceGenerator(name="subSeccionDispositivo_generator", sequenceName = "iot.sub_seccion_dispositivo_ssd_id_seq", allocationSize = 1)
    @Column(name = "ssd_id", nullable = false)
    private Long id;

    @Column(name = "ssd_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name= "ssd_descripcion", length = 2048)
    private String descripcion;
    
    @ManyToOne
    @JoinColumn(name = "ssd_sub_seccion_id", referencedColumnName = "sus_id", nullable = false)
    private Subseccion subseccion;

    @ManyToOne
    @JoinColumn(name="ssd_tipo_dispositivo_id", referencedColumnName="tid_id", nullable=false)
    private TipoDispositivo tipoDispositivo;

    @ManyToOne
    @JoinColumn(name = "ssd_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name="ssd_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;
    

    
}
