package com.coagronet.grupo;

import com.coagronet.empresa.Empresa;

import com.coagronet.estado.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grupo")
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gru_id")
    private Long id;

    @Column(name = "gru_nombre")
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gru_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;

    @Column(name = "gru_descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gru_estado_id", referencedColumnName = "est_id")
    private Estado estado;
}
