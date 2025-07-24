package com.coagronet.ordenCompra;

import java.time.LocalDateTime;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.pedido.Pedido;
import com.coagronet.proveedor.Proveedor;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orden_compra")
public class OrdenCompra {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orden_compra_generator")
	@SequenceGenerator(name = "orden_compra_generator", sequenceName = "orden_compra_orc_id_seq", allocationSize = 1)
	@Column(name = "orc_id")
	private Long id;

	@Column(name = "orc_fecha_hora")
	private LocalDateTime fechaHora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_pedido_id", referencedColumnName = "ped_id")
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_proveedor_id", referencedColumnName = "pro_id")
	private Proveedor proveedor;

	@Column(name = "orc_descripcion", length = 2048)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "orc_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
