package com.coagronet.presentacion;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "presentacion")
public class Presentacion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "presentacion_generator")
	@SequenceGenerator(name = "presentacion_generator", sequenceName = "presentacion_pre_id_seq", allocationSize = 1)
	@Column(name = "pre_id")
	private Long id;

	@Column(name = "pre_nombre", length = 255)
	private String nombre;

	@Column(name = "pre_descripcion", length = 500)
	private String descripcion;

	@ManyToOne
	@JoinColumn(name = "pre_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne
	@JoinColumn(name = "pre_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
