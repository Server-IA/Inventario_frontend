package com.coagronet.tipomodulo;

import java.io.Serializable;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tipo_modulo", schema = "public")
@SequenceGenerator(name = "TIM_SEQ", sequenceName = "tipo_modulo_tim_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "estado" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TipoModulo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIM_SEQ")
    @Column(name = "tim_id")
    private Long id;

    @Column(name = "tim_nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "tim_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tim_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "tipo_modulo_tim_estado_id_fkey"))
    private Estado estado;

}
