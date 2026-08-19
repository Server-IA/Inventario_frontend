package com.inventario.tipoBloque;

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
@Table(name = "tipo_bloque")
public class TipoBloque {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_bloque_generator")
	@SequenceGenerator(name = "tipo_bloque_generator", sequenceName = "tipo_bloque_tib_id_seq", allocationSize = 1)
	@Column(name = "tib_id", nullable = false)
	private Long id;

	@Column(name = "tib_nombre", length = 100)
	private String nombre;

	@Column(name = "tib_descripcion", length = 255)
	private String descripcion;

	@ManyToOne
	@JoinColumn(name = "tib_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tib_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
