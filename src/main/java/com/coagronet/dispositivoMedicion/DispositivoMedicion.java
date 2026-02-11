package com.coagronet.dispositivoMedicion;

import java.sql.Timestamp;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.subSeccionDispositivo.SubseccionDispositivo;
import com.coagronet.tipoMedicion.TipoMedicion;

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

@Data @Builder @Entity @NoArgsConstructor @AllArgsConstructor @Table(name = "dispositivo_medicion", schema = "iot")
public class DispositivoMedicion {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispositivoMedicion_generator") @SequenceGenerator(name = "dispositivoMedicion_generator", sequenceName = "iot.dispositivo_medicion_dim_id_seq", allocationSize = 1) @Column(name = "dim_id", nullable = false)
    private Long id;

    @Column(name = "dim_valor", nullable = false)
    private Double valor;

    @Column(name = "dim_nombre", nullable = false)
    private String nombre;

    @Column(name = "dim_descripcion", nullable = false)
    private String descripcion;

    @Column(name = "dim_fecha_hora", nullable = false)
    private Timestamp fechaHora;

    @ManyToOne @JoinColumn(name = "dim_sub_seccion_dispositivo_id", referencedColumnName = "ssd_id", nullable = false)
    private SubseccionDispositivo subseccionDispositivo;

    @ManyToOne @JoinColumn(name = "dim_tipo_medicion_id", referencedColumnName = "tim_id", nullable = false)
    private TipoMedicion tipoMedicion;

    @ManyToOne @JoinColumn(name = "dim_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne @JoinColumn(name = "dim_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;

}