package com.inventario.proveedor;

import java.time.LocalDateTime;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;

import com.inventario.tipoIdentificacion.TipoIdentificacion;
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
@Table(name = "proveedor")
public class Proveedor {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proveedor_generator")
	@SequenceGenerator(name = "proveedor_generator", sequenceName = "proveedor_pro_id_seq", allocationSize = 1)
	@Column(name = "pro_id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id")
	private Empresa empresa;

	@Column(name = "pro_fecha_creacion")
	private LocalDateTime fechaCreacion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id")
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_tipo_identificacion_id", referencedColumnName = "tii_id")
	private TipoIdentificacion tipoIdentificacion;

	@Column(name = "pro_nombre")
	private String nombre;

	@Column(name = "pro_celular")
	private String celular;

	@Column(name = "pro_contacto")
	private String contacto;

	@Column(name = "pro_correo")
	private String correo;

	@Column(name = "pro_identificacion")
	private String identificacion;

}
