package com.inventario.producto;

import java.io.Serializable;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.productoCategoria.ProductoCategoria;

import com.inventario.unidad.Unidad;
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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "producto", schema = "public")
@SequenceGenerator(name = "PRO_SEQ", sequenceName = "producto_pro_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "productoCategoria", "estado", "empresa", "unidadMinima" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Producto implements Serializable {

	private static final long serialVersionUID = 2L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRO_SEQ")
	@Column(name = "pro_id")
	private Long id;

	@Column(name = "pro_nombre", nullable = false, length = 100)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pro_producto_categoria_id", referencedColumnName = "prc_id", nullable = false, foreignKey = @ForeignKey(name = "producto_pro_producto_categoria_id_fkey"))
	private ProductoCategoria productoCategoria;

	@Column(name = "pro_descripcion", nullable = true, length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "producto_pro_estado_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id", nullable = false, foreignKey = @ForeignKey(name = "producto_pro_empresa_id_fkey"))
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pro_unidad_minima_id", referencedColumnName = "uni_id", nullable = false, foreignKey = @ForeignKey(name = "producto_pro_unidad_minima_fkey"))
	private Unidad unidadMinima;

	@Column(name = "pro_cantidad_minima", nullable = true)
	private Integer cantidadMinima;

	@Column(name = "pro_organico", nullable = true)
	private Boolean esOrganico;

}
