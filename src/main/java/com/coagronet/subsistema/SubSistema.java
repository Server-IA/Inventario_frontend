package com.coagronet.subsistema;

import java.io.Serializable;

import com.coagronet.estado.Estado;
import com.coagronet.tipoaplicacion.TipoAplicacion;

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
@Table(name = "subsistema", schema = "public")
@SequenceGenerator(name = "SUB_SEQ", sequenceName = "subsistema_sub_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "estado", "tipoAplicacion" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SubSistema implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUB_SEQ")
    @Column(name = "sub_id")
    private Long id;

    @Column(name = "sub_nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "sub_icon", nullable = false, length = 255)
    private String icon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sub_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "subsistema_sub_estado_id_fkey"))
    private Estado estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "sub_tipo_aplicacion_id", referencedColumnName = "tia_id", nullable = true, foreignKey = @ForeignKey(name = "subsistema_sub_tipo_aplicacion_id_fkey"))
    private TipoAplicacion tipoAplicacion;

}
