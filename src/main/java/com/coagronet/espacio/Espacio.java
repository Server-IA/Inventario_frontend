package com.coagronet.espacio;

import com.coagronet.bloque.Bloque;
import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.tipoEspacio.TipoEspacio;

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
@Table(name = "espacio", schema = "public")
public class Espacio {

	@Id
	@SequenceGenerator(name = "espacio_generator", sequenceName = "espacio_esp_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "espacio_generator")
	@Column(name = "esp_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_bloque_id", referencedColumnName = "blo_id", nullable = false)
	private Bloque bloque;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_tipo_espacio_id", referencedColumnName = "tie_id", nullable = false)
	private TipoEspacio tipoEspacio;

	@Column(name = "esp_nombre", length = 100)
	private String nombre;

	@Column(name = "esp_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

	@Column(name = "esp_geolocalizacion", length = 255)
	private String geolocalizacion;

	@Column(name = "esp_coordenadas", length = 255)
	private String coordenadas;

	@Column(name = "esp_direccion", length = 255)
	private String direccion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "esp_empresa_id", referencedColumnName = "emp_id", nullable = false)
	private Empresa empresa;

}