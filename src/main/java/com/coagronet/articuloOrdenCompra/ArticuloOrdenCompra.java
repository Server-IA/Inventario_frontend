package com.coagronet.articuloOrdenCompra;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.ordenCompra.OrdenCompra;
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
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orden_compra_item", schema = "public")
public class ArticuloOrdenCompra {

	@Id
	@SequenceGenerator(name = "orden_compra_item_generator", sequenceName = "orden_compra_item_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_compra_item_generator")
	@Column(name = "oci_id")
	private Long id;

	@Column(name = "oci_cantidad")
	private Double cantidad;

	@Column(name = "oci_precio")
	private Double precio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "oci_orden_compra_id", referencedColumnName = "orc_id", nullable = false,
			foreignKey = @ForeignKey(name = "orden_compra_itemorden_compraorc_id_fkey"))
	private OrdenCompra ordenCompra;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "oci_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false,
			foreignKey = @ForeignKey(name = "orden_compra_itemproducto_presentacionprp_id_fkey"))
	private PresentacionProducto presentacionProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "oci_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "orden_compra_itemestadoest_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "oci_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "orden_compra_item_oci_empresa_id_fkey"))
	private Empresa empresa;

}