package com.coagronet.articuloKardex;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.kardex.Kardex;
import com.coagronet.presentacionProducto.PresentacionProducto;

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
@Table(name = "kardex_item", schema = "public")
public class ArticuloKardex {

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kardex_item_generator")
	@SequenceGenerator(name = "kardex_item_generator", sequenceName = "kardex_item_kai_id_seq", allocationSize = 1)
	@Column(name = "kai_id")
	private Long id;

	@Column(name = "kai_cantidad")
	private Double cantidad;

	@Column(name = "kai_precio")
	private Double precio;

	@Column(name = "kai_fecha_vencimiento")
	private LocalDateTime fechaVencimiento;

	@Column(name = "kai_producto_identificador")
	private String identificadorProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_kardex_id", referencedColumnName = "kar_id", nullable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_kardex_id_fkey"))
	private Kardex kardex;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_producto_presentacion_id", referencedColumnName = "prp_id", nullable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_producto_presentacion_id_fkey"))
	private PresentacionProducto presentacionProducto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_estado_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kai_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "kardex_item_kai_empresa_id_fkey"))
	private Empresa empresa;

	@Column(name = "kai_lote")
	private String lote;

	@Column(name = "kai_seg_username")
	private String username;

	@Column(name = "kai_seg_rol")
	private String rol;

	@Column(name = "kai_seg_ip")
	private String ip;

	@Column(name = "kai_seg_host")
	private String host;

	@Column(name = "kai_seg_fecha_hora", insertable = false, updatable = false)
	private OffsetDateTime fechaHora;


	@PrePersist
	public void prePersist() {
		if (identificadorProducto == null || identificadorProducto.isBlank()) {
			this.identificadorProducto = UUID.randomUUID().toString();
		}
	}

}