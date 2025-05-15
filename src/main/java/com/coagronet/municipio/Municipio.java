package com.coagronet.municipio;

import com.coagronet.departamento.Departamento;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@NoArgsConstructor
@Setter
@Entity
@Table(name = "municipio", schema = "public")
public class Municipio {

	@Id
	@SequenceGenerator(name = "municipio_generator", sequenceName = "municipio_mun_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipio_generator")
	@Column(name = "mun_id")
	private Long id;

	@Column(name = "mun_nombre", length = 60, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mun_departamento_id", referencedColumnName = "dep_id", nullable = false)
	private Departamento departamento;

	@Column(name = "mun_codigo")
	private Integer codigo;

	@Column(name = "mun_acronimo", length = 3)
	private String acronimo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mun_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mun_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

}
