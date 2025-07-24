package com.coagronet.tipoProduccion;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;

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
@Table(name = "tipo_produccion", schema = "public")
public class TipoProduccion {

	@Id
	@SequenceGenerator(name = "tipo_produccion_generator", sequenceName = "tipo_produccion_tip_id_seq",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_produccion_generator")
	@Column(name = "tip_id")
	private Long id;

	@Column(name = "tip_nombre", length = 100, nullable = false)
	private String nombre;

	@Column(name = "tip_descripcion", length = 255)
	private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tip_estado_id", referencedColumnName = "est_id", nullable = false,
			foreignKey = @ForeignKey(name = "tipo_produccionestadoest_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tip_empresa_id", referencedColumnName = "emp_id", nullable = false,
			foreignKey = @ForeignKey(name = "tipo_produccion_tip_empresa_fkey"))
	private Empresa empresa;

}
