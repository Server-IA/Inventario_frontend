package com.coagronet.producto;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.productoCategoria.ProductoCategoria;

import com.coagronet.unidad.Unidad;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto", schema = "public")
public class Producto {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_generator")
	@SequenceGenerator(name = "producto_generator", sequenceName = "producto_pro_id_seq", allocationSize = 1)
	@Column(name = "pro_id", nullable = false)
	private Long id;

	@Column(name = "pro_nombre")
	private String nombre;

	@Column(name = "pro_descripcion")
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_producto_categoria_id", referencedColumnName = "prc_id")
	private ProductoCategoria productoCategoria;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_unidad_minima_id", referencedColumnName = "uni_id")
	private Unidad unidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
