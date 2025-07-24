package com.coagronet.subseccion;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.seccion.Seccion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sub_seccion")
public class Subseccion {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_seccion_seq")
	@SequenceGenerator(name = "sub_seccion_seq", sequenceName = "sub_seccion_sus_id_seq", allocationSize = 1)
	@Column(name = "sus_id")
	private Long id;

	@Column(name = "sus_nombre")
	private String nombre;

	@Column(name = "sus_descripcion")
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sus_estado_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sus_empresa_id")
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sus_seccion_id")
	private Seccion seccion;

}
