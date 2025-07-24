package com.coagronet.ingredientePresentacionProducto;

import java.io.Serializable;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.ingrediente.Ingrediente;
import com.coagronet.presentacionProducto.PresentacionProducto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto_presentacion_ingrediente", schema = "public")
public class IngredientePresentacionProducto implements Serializable {

	private static final long serialVersionUID = 2646372266109775341L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ppi_seq_gen")
	@SequenceGenerator(name = "ppi_seq_gen", sequenceName = "producto_presentacion_ingrediente_ppi_id_seq",
			allocationSize = 1)
	@Column(name = "ppi_id")
	private Long id;

	@Column(name = "ppi_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "ppi_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ppi_ingrediente_id", referencedColumnName = "ing_id", nullable = false,
			foreignKey = @ForeignKey(name = "producto_presentacion_ingrediente_ppi_ingrediente_id_fkey"))
	private Ingrediente ingrediente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ppi_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false,
			foreignKey = @ForeignKey(name = "producto_presentacion_ingrediente_ppi_producto_presentacion_id_"))
	private PresentacionProducto presentacionProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ppi_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "producto_presentacion_ingrediente_ppi_empresa_id_fkey"))
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ppi_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "producto_presentacion_ingrediente_ppi_estado_id_fkey"))
	private Estado estado;

}