package com.coagronet.kardex;

import java.time.LocalDateTime;

import com.coagronet.almacen.Almacen;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.produccion.Produccion;
import com.coagronet.tipoMovimiento.TipoMovimiento;

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
@Table(name = "kardex")
public class Kardex {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kardex_generator")
	@SequenceGenerator(name = "kardex_generator", sequenceName = "kardex_kar_id_seq", allocationSize = 1)
	@Column(name = "kar_id", nullable = false)
	private Long id;

	@Column(name = "kar_fecha_hora")
	private LocalDateTime fechaHora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_almacen_id", referencedColumnName = "alm_id")
	private Almacen almacen;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_produccion_id", referencedColumnName = "pro_id")
	private Produccion produccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_tipo_movimiento_id", referencedColumnName = "tim_id")
	private TipoMovimiento tipoMovimiento;

	@Column(name = "kar_descripcion", length = 500)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kar_cliente_proveedor_id", referencedColumnName = "emp_id")
	private Empresa clienteProveedor;
}
