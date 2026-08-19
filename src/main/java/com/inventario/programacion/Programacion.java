package com.inventario.programacion;


import java.time.OffsetTime;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.subSeccionDispositivo.SubseccionDispositivo;

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
@Table(name = "programacion", schema = "iot")
public class Programacion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programacion_generator")
    @SequenceGenerator(name = "programacion_generator", sequenceName = "iot.programacion_pro_id_seq", allocationSize = 1)
    @Column(name = "pro_id", nullable = false)
    private Long id;

    @Column(name = "pro_descripcion", length = 2048)
    private String descripcion;

    @Column(name = "pro_comando", length = 255, nullable = false)
    private String comando;

    @Column(name = "pro_fecha_hora", nullable = false)
    private OffsetTime fechaHora;
    
    @ManyToOne
    @JoinColumn(name = "pro_sub_seccion_dispositivo_id", referencedColumnName = "ssd_id", nullable = false)
    private SubseccionDispositivo subseccionDispositivo;

    @ManyToOne
    @JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id", nullable= false)
    private Empresa empresa;
    
}
