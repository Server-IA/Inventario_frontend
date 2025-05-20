package com.coagronet.almacen;

import com.coagronet.empresa.Empresa;
import com.coagronet.espacio.Espacio;
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
@Table(name = "almacen", schema = "public")
public class Almacen {

	@Id
	@SequenceGenerator(name = "almacen_generator", sequenceName = "almacen_alm_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "almacen_generator")
	@Column(name = "alm_id")
	private Long id;

	@Column(name = "alm_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "alm_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alm_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@Column(name = "alm_geolocalizacion", length = 255)
	private String geolocalizacion;

	@Column(name = "alm_coordenadas", columnDefinition = "text", length = 4096)
	private String coordenadas;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alm_espacio_id", referencedColumnName = "esp_id", nullable = false)
	private Espacio espacio;

	@Column(name = "alm_direccion", length = 255)
	private String direccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "alm_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

}