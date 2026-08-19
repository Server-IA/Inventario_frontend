package com.inventario.controlInventario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vista_empresa_inventarios")
public class VistaEmpresaInventario {

	@Id
	@Column(name = "inv_id")
	private Long invId;

	@Column(name = "sed_nombre")
	private String sedNombre;

	@Column(name = "blo_nombre")
	private String bloNombre;

	@Column(name = "esp_nombre")
	private String espNombre;

	@Column(name = "sec_nombre")
	private String secNombre;

	@Column(name = "sus_nombre")
	private String susNombre;

	@Column(name = "tii_nombre")
	private String tiiNombre;

	@Column(name = "sed_empresa_id")
	private Long sedEmpresaId;

	@Column(name = "inv_nombre")
	private String invNombre;

	@Column(name = "inv_descripcion")
	private String invDescripcion;

	@Column(name = "inv_fecha_hora")
	private LocalDateTime invFechaHora;

	@Column(name = "inv_tipo_inventario_id")
	private Long invTipoInventarioId;

	@Column(name = "inv_sub_seccion_id")
	private Long invSubSeccionId;

	@Column(name = "inv_empresa_id")
	private Long invEmpresaId;

	@Column(name = "inv_estado_id")
	private Long invEstadoId;

}
