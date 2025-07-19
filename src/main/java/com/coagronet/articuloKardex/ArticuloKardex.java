package com.coagronet.articuloKardex;

import java.time.LocalDateTime;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.kardex.Kardex;
import com.coagronet.presentacionProducto.PresentacionProducto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
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
@Table(name = "kardex_item", schema = "public")
public class ArticuloKardex {

	@Id
	@SequenceGenerator(name = "kardex_item_generator", sequenceName = "kardex_item_kai_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kardex_item_generator")
	@Column(name = "kai_id")
	private Long id;

	@Column(name = "kai_cantidad")
	private Double cantidad;

	@Column(name = "kai_precio")
	private Double precio;

	@Column(name = "kai_fecha_vencimiento")
	private LocalDateTime fechaVencimiento;

	@Column(name = "kai_producto_identificador")
	private String productoIdentificador;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_kardex_id", referencedColumnName = "kar_id", nullable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_kardex_id_fkey"))
	private Kardex kardex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_producto_presentacion_id_fkey"))
	private PresentacionProducto presentacionProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_estado_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_empresa_id", referencedColumnName = "emp_id", nullable = false, foreignKey = @ForeignKey(name = "kardex_item_kai_empresa_id_fkey"))
	private Empresa empresa;

	@Column(name = "kai_lote")
	private String lote;

}