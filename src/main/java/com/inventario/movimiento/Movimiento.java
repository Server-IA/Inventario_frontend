package com.inventario.movimiento;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movimiento", schema = "public")
public class Movimiento {

	@Id
	@SequenceGenerator(name = "movimiento_generator", sequenceName = "movimiento_mov_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movimiento_generator")
	@Column(name = "mov_id")
	private Long id;

	@Column(name = "mov_nombre", length = 25, nullable = false)
	private String nombre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mov_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "movimiento_mov_estado_id_fkey"))
	private Estado estado;

}
