package com.inventario.facturacion;

import java.time.LocalDate;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;

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
@Table(name = "facturacion")
public class Facturacion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "facturacion_generator")
	@SequenceGenerator(name = "facturacion_generator", sequenceName = "facturacion_fac_id_seq", allocationSize = 1)
	@Column(name = "fac_id")
	private Long id;

	@Column(name = "fac_prefijo", length = 10, nullable = false)
	private String prefijo;

	@Column(name = "fac_numero_inicial", nullable = false)
	private Long numeroInicial;

	@Column(name = "fac_cantidad", nullable = false)
	private Integer cantidad;

	@Column(name = "fac_fecha_inicio", nullable = false)
	private LocalDate fechaInicio;

	@Column(name = "fac_fecha_fin", nullable = false)
	private LocalDate fechaFin;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fac_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fac_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
