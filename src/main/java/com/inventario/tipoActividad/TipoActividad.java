package com.inventario.tipoActividad;

import com.inventario.categoriaActividad.CategoriaActividad;
import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;
import com.inventario.proceso.Proceso;

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
@Table(name = "tipo_actividad", schema = "public")
public class TipoActividad {

	@Id
	@SequenceGenerator(name = "tipo_actividad_generator", sequenceName = "tipo_actividad_tia_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_actividad_generator")
	@Column(name = "tia_id")
	private Long id;

	@Column(name = "tia_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "tia_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tia_categoria_actividad_id", referencedColumnName = "caa_id", nullable = false,
			foreignKey = @ForeignKey(name = "tipo_actividadcategoria_actividadcaa_id_fkey"))
	private CategoriaActividad categoriaActividad;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tia_proceso_id", referencedColumnName = "pro_id", nullable = false,
			foreignKey = @ForeignKey(name = "tipo_actividadprocesopro_id_fkey"))
	private Proceso proceso;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tia_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "tipo_actividadestadoest_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tia_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "tipo_actividad_tia_empresa_id_fkey"))
	private Empresa empresa;

}
