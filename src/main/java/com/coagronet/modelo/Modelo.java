package com.coagronet.modelo;

import java.sql.Timestamp;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.producto.Producto;
import com.coagronet.tipoModelo.TipoModelo;

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
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "modelo", schema = "public")
public class Modelo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "modelo_generator")
    @SequenceGenerator(name = "modelo_generator", sequenceName = "modelo_mod_id_seq", allocationSize = 1)
    @Column(name = "mod_id", nullable = false)
    private Long id;
    
    @Column(name = "mod_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "mod_descripcion", length = 2048)
    private String descripcion;

    @Column(name = "mod_url", length = 1024, nullable = false)
    private String url;

    @Column(name = "mod_fecha_hora_inicio", nullable = false)
    private Timestamp fechaHoraInicio;

    @Column(name = "mod_fecha_hora_fin", nullable = false)
    private Timestamp fechaHoraFin;

    @Column(name = "mod_fecha_hora_creacion", nullable = false)
    private Timestamp fechaHoraCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_tipo_modelo_id", referencedColumnName = "tim_id", nullable = false)
    private TipoModelo tipoModelo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_producto_id", referencedColumnName = "pro_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mod_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;



    
}

