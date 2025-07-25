package com.coagronet.controlInventario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "vista_inventario_productos")
public class VistaInventarioProducto {

	@Id
	@Column(name = "pro_id")
	private Long proId;

	@Column(name = "prc_nombre")
	private String prcNombre;

	@Column(name = "mar_nombre")
	private String marNombre;

	@Column(name = "uni_nombre")
	private String uniNombre;

	@Column(name = "pre_nombre")
	private String preNombre;

	@Column(name = "prp_nombre")
	private String prpNombre;

	@Column(name = "pro_nombre")
	private String proNombre;

	@Column(name = "pro_producto_categoria_id")
	private Long proProductoCategoriaId;

	@Column(name = "pro_descripcion")
	private String proDescripcion;

	@Column(name = "pro_estado_id")
	private Long proEstadoId;

	@Column(name = "pro_empresa_id")
	private Long proEmpresaId;

	@Column(name = "pro_unidad_minima_id")
	private Long proUnidadMinimaId;

	@Column(name = "kar_empresa_id")
	private Long karEmpresaId;

	@Column(name = "prl_sub_seccion_id")
	private Long prlSubSeccionId;

}
