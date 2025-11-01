package com.coagronet.unidad;

import com.coagronet.estado.Estado;

import com.coagronet.tipounidad.TipoUnidad;
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
@Table(name = "unidad")
public class Unidad {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidad_generator")
	@SequenceGenerator(name = "unidad_generator", sequenceName = "unidad_uni_id_seq", allocationSize = 1)
	@Column(name = "uni_id", nullable = false)
	private Long id;

	@Column(name = "uni_nombre")
	private String nombre;

	@Column(name = "uni_descripcion", length = 500)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uni_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "uni_tipo_unidad_id", referencedColumnName = "tiu_id")
	private TipoUnidad tipoUnidad;


}
