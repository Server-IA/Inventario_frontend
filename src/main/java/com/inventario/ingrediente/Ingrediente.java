package com.inventario.ingrediente;

import com.inventario.empresa.Empresa;
import com.inventario.estado.Estado;

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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ingrediente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingrediente {

	@Id
	@SequenceGenerator(name = "ingrediente_generator", sequenceName = "ingrediente_ing_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ingrediente_generator")
	@Column(name = "ing_id")
	private Long id;

	@NotBlank
	@Column(name = "ing_nombre", nullable = false, length = 255)
	private String nombre;

	@Column(name = "ing_descripcion", length = 255)
	private String descripcion;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ing_estado_id", nullable = false,
			foreignKey = @ForeignKey(name = "ingrediente_ing_estado_id_fkey"))
	private Estado estado;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ing_empresa_id", nullable = false,
			foreignKey = @ForeignKey(name = "ingrediente_ing_empresa_id_fkey"))
	private Empresa empresa;

}
