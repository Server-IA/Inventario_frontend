package com.coagronet.modulo;

import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.coagronet.estado.Estado;
import com.coagronet.subsistema.SubSistema;
import com.coagronet.tipoaplicacion.TipoAplicacion;
import com.coagronet.tipomodulo.TipoModulo;

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
@Table(name = "modulo", schema = "public")
@SequenceGenerator(name = "MOD_SEQ", sequenceName = "modulo_mod_id_seq", schema = "public", initialValue = 1, allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "estado", "subSistema", "tipoModulo", "tipoAplicacion" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Modulo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MOD_SEQ")
    @Column(name = "mod_id")
    private Long id;

    @Column(name = "mod_nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "mod_url", nullable = false, length = 100)
    private String url;

    @Column(name = "mod_descripcion", length = 2048)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mod_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_estado_id_fkey"))
    private Estado estado;

    @Column(name = "mod_icon", length = 255)
    private String icon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mod_subsistema_id", referencedColumnName = "sub_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_subsistema_id_fkey"))
    private SubSistema subSistema;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mod_tipo_modulo_id", referencedColumnName = "tim_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_tipo_modulo_id_fkey"))
    private TipoModulo tipoModulo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mod_tipo_aplicacion_id", referencedColumnName = "tia_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_tipo_aplicacion_id_fkey"))
    private TipoAplicacion tipoAplicacion;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "mod_rol_id", columnDefinition = "varchar(255)[]")
    private String[] rolId;

    @Column(name = "mod_nombre_id", length = 255)
    private String nombreId;

}
