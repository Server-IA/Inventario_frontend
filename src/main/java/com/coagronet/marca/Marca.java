package com.coagronet.marca;

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

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "marca")
public class Marca {

	@Id
	@SequenceGenerator(name = "marca_generator", sequenceName = "marca_mar_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "marca_generator")
	@Column(name = "mar_id")
	private Long id;

	@Column(name = "mar_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "mar_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mar_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mar_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

}
