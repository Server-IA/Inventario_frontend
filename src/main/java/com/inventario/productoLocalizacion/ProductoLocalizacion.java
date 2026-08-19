package com.inventario.productoLocalizacion;

import com.inventario.articuloKardex.ArticuloKardex;
import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.subseccion.Subseccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "producto_localizacion", schema = "public")
public class ProductoLocalizacion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "producto_localizacion_generator")
	@SequenceGenerator(name = "producto_localizacion_generator", sequenceName = "producto_localizacion_prl_id_seq",
			allocationSize = 1)
	@Column(name = "prl_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prl_kardex_item_id", referencedColumnName = "kai_id")
	private ArticuloKardex articuloKardex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prl_sub_seccion_id", referencedColumnName = "sus_id")
	private Subseccion subseccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prl_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prl_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
