package com.inventario.categoriaestado;

import java.io.Serializable;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "estado_categoria", schema = "public")
public class CategoriaEstado implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estado_categoria_seq")
	@SequenceGenerator(name = "estado_categoria_seq", sequenceName = "estado_categoria_esc_id_seq", allocationSize = 1)
	@Column(name = "esc_id")
	private Long id;

	@Column(name = "esc_nombre", length = 100)
	@NotBlank(message = "{validation.nombre.not-blank}")
	@Length(max = 100, message = "{validation.nombre.length}")
	private String nombre;

	@Column(name = "esc_descripcion", length = 2048)
	@Length(max = 2048, message = "{validation.descripcion.length}")
	private String descripcion;

}
