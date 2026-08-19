package com.inventario.tipoDispositivo;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;

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
@Table(name = "tipo_dispositivo", schema = "iot")
public class TipoDispositivo {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="tipoDispositivo_generator")
    @SequenceGenerator(name="tipoDispositivo_generator", sequenceName = "iot.tipo_dispositivo_tid_id_seq", allocationSize = 1)
    @Column(name = "tid_id", nullable = false)
    private Long id;

    @Column(name = "tid_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name= "tid_descripcion", length = 2048, nullable = false)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "tid_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "tid_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;
    
}
