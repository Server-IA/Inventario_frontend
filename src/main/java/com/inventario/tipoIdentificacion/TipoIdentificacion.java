package com.inventario.tipoIdentificacion;

import com.inventario.estado.Estado;

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
@Table(name = "tipo_identificacion")
public class TipoIdentificacion {

	@Id
	@SequenceGenerator(name = "tipo_identificacion_sequence", sequenceName = "tipo_identificacion_tii_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_identificacion_sequence")
	@Column(name = "tii_id")
	private Long id;

	@Column(name = "tii_nombre", length = 255)
	private String nombre;

	@Column(name = "tii_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tii_estado_id", referencedColumnName = "est_id", nullable = false)
	private Estado estado;

}
