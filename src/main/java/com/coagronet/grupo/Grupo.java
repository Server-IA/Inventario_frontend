package com.coagronet.grupo;

import com.coagronet.empresa.Empresa;

import com.coagronet.estado.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@Entity
@Table(name = "grupo", schema = "public")
public class Grupo {

	@Id
	@SequenceGenerator(name = "grupo_generator", sequenceName = "grupo_gru_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grupo_generator")
	@Column(name = "gru_id")
	private Long id;

	@Column(name = "gru_nombre", length = 255, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gru_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

	@Column(name = "gru_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gru_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

}