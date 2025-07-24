package com.coagronet.articuloPedido;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.pedido.Pedido;
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
@Table(name = "pedido_item", schema = "public")
public class ArticuloPedido {

	@Id
	@SequenceGenerator(name = "pedido_item_generator", sequenceName = "pedido_item_pei_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_item_generator")
	@Column(name = "pei_id")
	private Long id;

	@Column(name = "pei_cantidad")
	private Double cantidad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pei_pedido_id", referencedColumnName = "ped_id", nullable = false,
			foreignKey = @ForeignKey(name = "pedido_itempedidoped_id_fkey"))
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pei_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false,
			foreignKey = @ForeignKey(name = "pedido_itemproducto_presentacionprp_id_fkey"))
	private PresentacionProducto presentacionProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pei_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "pedido_itemestadoest_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pei_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "pedido_item_pei_empresa_id_fkey"))
	private Empresa empresa;

}