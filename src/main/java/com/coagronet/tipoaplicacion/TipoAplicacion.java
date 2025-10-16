package com.coagronet.tipoaplicacion;

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
@Table(name = "tipo_aplicacion", schema = "public")
@SequenceGenerator(name = "TIA_SEQ", sequenceName = "tipo_aplicacion_tia_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "estado" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TipoAplicacion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIA_SEQ")
    @Column(name = "tia_id")
    private Long id;

    @Column(name = "tia_nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "tia_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "tia_estado_id", referencedColumnName = "est_id", nullable = true, foreignKey = @ForeignKey(name = "tipo_aplicacion_tia_estado_id_fkey"))
    private Estado estado;

}
