package com.coagronet.proceso;

import com.coagronet.empresa.Empresa;
import com.coagronet.estado.Estado;
import com.coagronet.tipoProduccion.TipoProduccion;

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
@Table(name = "proceso", schema = "public")
public class Proceso {

	@Id
	@SequenceGenerator(name = "proceso_generator", sequenceName = "proceso_pro_id_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proceso_generator")
	@Column(name = "pro_id")
	private Long id;
	
	@Column(name = "pro_nombre", length = 100, nullable = false)
    private String nombre;

    @Column(name = "pro_descripcion", length = 255)
    private String descripcion;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_tipo_produccion_id", referencedColumnName = "tip_id", nullable = false, foreignKey = @ForeignKey(name = "procesotipo_producciontip_id_fkey"))
	private TipoProduccion tipoProduccion;    

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "procesoestadoest_id_fkey"))
	private Estado estado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pro_empresa_id", referencedColumnName = "emp_id", nullable = false, foreignKey = @ForeignKey(name = "proceso_pro_empresa_id_fkey"))
	private Empresa empresa;
}
