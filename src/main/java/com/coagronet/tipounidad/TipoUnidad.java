package com.coagronet.tipounidad;

import com.coagronet.estado.Estado;
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
@Table(name = "tipo_unidad")
public class TipoUnidad {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_unidad_generator")
	@SequenceGenerator(name = "tipo_unidad_generator", sequenceName = "tipo_unidad_tiu_id_seq", allocationSize = 1)
	@Column(name = "tiu_id", nullable = false)
	private Long id;

	@Column(name = "tiu_nombre")
	private String nombre;

	@Column(name = "tiu_descripcion", length = 500)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tiu_estado_id", referencedColumnName = "est_id")
	private Estado estado;



}
