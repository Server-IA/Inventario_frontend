package com.coagronet.pedidocotizacion;

import com.coagronet.estado.Estado;
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
import lombok.Data;

@Entity
@Table(name = "pedido_cotizacion", schema = "public")
@SequenceGenerator(name = "PEQ_SEQ", sequenceName = "pedido_cotizacion_pec_id_seq", schema = "public", initialValue = 1,
		allocationSize = 1)
@Data
public class PedidoCotizacion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PEQ_SEQ")
	@Column(name = "pec_id")
	private Long id;

	@Column(name = "pec_pedido_id", nullable = false)
	private Long pedidoId;

	@Column(name = "pec_descripcion", length = 2048)
	private String descripcion;

	@Column(name = "pec_archivo", length = 2048)
	private String archivo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pec_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pec_proveedor_id", referencedColumnName = "pro_id")
	private Proveedor proveedor;

}
