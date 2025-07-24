package com.coagronet.bloque;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.sede.Sede;
import com.coagronet.tipoBloque.TipoBloque;

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
@Table(name = "bloque", schema = "public")
public class Bloque {

	@Id
	@SequenceGenerator(name = "bloque_generator", sequenceName = "bloque_blo_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bloque_generator")
	@Column(name = "blo_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blo_sede_id", referencedColumnName = "sed_id", nullable = false)
	private Sede sede;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blo_tipo_bloque_id", referencedColumnName = "tib_id", nullable = false)
	private TipoBloque tipoBloque;

	@Column(name = "blo_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "blo_numero_pisos")
	private Integer numeroPisos;

	@Column(name = "blo_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blo_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@Column(name = "blo_geolocalizacion", length = 255)
	private String geolocalizacion;

	@Column(name = "blo_coordenadas", length = 255)
	private String coordenadas;

	@Column(name = "blo_direccion", length = 255)
	private String direccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blo_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

}