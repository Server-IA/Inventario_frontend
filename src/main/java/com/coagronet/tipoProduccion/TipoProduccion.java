package com.coagronet.tipoProduccion;

import com.coagronet.estado.Estado;

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
@Table(name = "tipo_produccion")
public class TipoProduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tipo_produccion_generator")
    @SequenceGenerator(name = "tipo_produccion_generator", sequenceName = "tipo_produccion_tip_id_seq", allocationSize = 1)
    @Column(name = "tip_id", nullable = false)
    private Integer id;

    @Column(name = "tip_nombre", length = 100)
    private String nombre;

    @Column(name = "tip_descripcion", length = 255)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tip_estado", referencedColumnName = "est_id")
    private Estado estado;
}
