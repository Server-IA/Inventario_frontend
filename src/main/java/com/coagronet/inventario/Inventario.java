package com.coagronet.inventario;

import java.time.LocalDateTime;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.subseccion.Subseccion;
import com.coagronet.tipoInventario.TipoInventario;

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
@Table(name = "inventario")
public class Inventario {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inventario_seq")
	@SequenceGenerator(name = "inventario_seq", sequenceName = "inventario_inv_id_seq", allocationSize = 1)
	@Column(name = "inv_id", nullable = false)
	private Long id;

	@Column(name = "inv_nombre")
	private String nombre;

	@Column(name = "inv_descripcion")
	private String descripcion;

	@Column(name = "inv_fecha_hora")
	private LocalDateTime fechaHora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inv_tipo_inventario_id", referencedColumnName = "tii_id")
	private TipoInventario tipoInventario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inv_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

	@ManyToOne
	@JoinColumn(name = "inv_sub_seccion_id", referencedColumnName = "sus_id")
	private Subseccion subseccion;

	@ManyToOne
	@JoinColumn(name = "inv_estado_id", referencedColumnName = "est_id")
	private Estado estado;

}
