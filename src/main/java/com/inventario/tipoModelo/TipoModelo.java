package com.inventario.tipoModelo;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;

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
@Table(name = "tipo_modelo", schema = "public")
public class TipoModelo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipoModelo_generator")
    @SequenceGenerator(name = "tipoModelo_generator", sequenceName = "tipo_modelo_tim_id_seq", allocationSize = 1)
    @Column(name = "tim_id", nullable = false)
    private Long id;

    @Column(name = "tim_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "tim_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tim_estado_id", referencedColumnName = "est_id", nullable = false)
    private Estado estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tim_empresa_id", referencedColumnName = "emp_id", nullable = false)
    private Empresa empresa;
    

    
}
