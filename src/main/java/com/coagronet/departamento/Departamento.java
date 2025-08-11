package com.coagronet.departamento;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.pais.Pais;

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

@Entity
@Table(name = "departamento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departamento {

	@Id
	@SequenceGenerator(name = "departamento_generator", sequenceName = "departamento_dep_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departamento_generator")
	@Column(name = "dep_id")
	private Long id;

	@Column(name = "dep_nombre", length = 70, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dep_pais_id", referencedColumnName = "pai_id", nullable = false)
	private Pais pais;

	@Column(name = "dep_codigo", nullable = false)
	private Integer codigo;

	@Column(name = "dep_acronimo", length = 3, nullable = false)
	private String acronimo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dep_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dep_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

}
