package com.coagronet.productoCategoria;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;

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
@Table(name = "producto_categoria")
public class ProductoCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_categoria_generator")
    @SequenceGenerator(name = "producto_categoria_generator", sequenceName = "producto_categoria_prc_id_seq", allocationSize = 1)
    @Column(name = "prc_id", nullable = false)
    private Long id;

    @Column(name = "prc_nombre")
    private String nombre;

    @Column(name = "prc_descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "prc_estado", referencedColumnName = "est_id")
    private Estado estado;

    @ManyToOne
    @JoinColumn(name = "prc_empresa_id", referencedColumnName = "emp_id")
    private Empresa empresa;
}
