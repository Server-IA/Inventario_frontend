package com.inventario.tipoInventario;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
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
@Table(name = "tipo_inventario")
public class TipoInventario {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_inventario_seq")
	@SequenceGenerator(name = "tipo_inventario_seq", sequenceName = "tipo_inventario_tii_id_seq", allocationSize = 1)
	@Column(name = "tii_id")
	private Long id;

	@Column(name = "tii_nombre")
	private String nombre;

	@Column(name = "tii_descripcion")
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tii_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tii_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

}
