package com.inventario.productoCategoria;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@Entity
@Table(name = "producto_categoria", schema = "public")
public class ProductoCategoria {

	@Id
	@SequenceGenerator(name = "producto_categoria_generator", sequenceName = "producto_categoria_prc_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_categoria_generator")
	@Column(name = "prc_id")
	private Long id;

	@Column(name = "prc_nombre", length = 255, nullable = false)
	private String nombre;

	@Column(name = "prc_descripcion", length = 255)
	private String descripcion;

	@ManyToOne
	@JoinColumn(name = "prc_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "producto_categoria_prc_estado_id_fkey"))
	private Estado estado;

	@ManyToOne
	@JoinColumn(name = "prc_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "producto_categoria_prc_empresa_id_fkey"))
	private Empresa empresa;

}