package com.coagronet.pedidocotizacion;

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

/**
 * Entidad JPA que representa una cotización asociada a un
 * {@link com.coagronet.pedido.Pedido}.
 *
 * <p>
 * Una {@code PedidoCotizacion} almacena una descripción libre, una referencia
 * de archivo
 * (por ejemplo, URL o nombre de archivo en un bucket) y las asociaciones con el
 * {@link com.coagronet.pedido.Pedido}, el
 * {@link com.coagronet.proveedor.Proveedor} y el
 * {@link com.coagronet.estado.Estado} que describe su estado actual en el
 * flujo.
 * </p>
 *
 * <p>
 * Convenciones de persistencia (por ejemplo, nombres de columnas como
 * {@code pec_id},
 * {@code pec_descripcion}, {@code pec_archivo}) se configuran mediante
 * anotaciones JPA y pueden
 * consultarse en las anotaciones de los campos.
 * </p>
 *
 * <h2>Invariantes y reglas</h2>
 * <ul>
 * <li>Cada registro pertenece a una empresa a través del pedido asociado.</li>
 * <li>Longitudes máximas de texto (por ejemplo, 2048 caracteres) deben
 * respetarse según el esquema.</li>
 * <li>Las asociaciones se cargan perezosamente ({@code FetchType.LAZY}).</li>
 * </ul>
 *
 * @author Juan J. Castro
 * @since 0.3.1
 */

@Entity
@Table(name = "pedido_cotizacion", schema = "public")
@SequenceGenerator(name = "PEQ_SEQ", sequenceName = "pedido_cotizacion_pec_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCotizacion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PEQ_SEQ")
	@Column(name = "pec_id")
	private Long id;

	@Column(name = "pec_descripcion", length = 2048)
	private String descripcion;

	@Column(name = "pec_archivo", length = 2048)
	private String archivo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pec_pedido_id", referencedColumnName = "ped_id")
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pec_proveedor_id", referencedColumnName = "pro_id")
	private Proveedor proveedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pec_estado_id", referencedColumnName = "est_id")
	private Estado estado;

}
