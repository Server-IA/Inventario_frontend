package com.coagronet.subsistema;

import java.io.Serializable;

import com.coagronet.estado.Estado;
import com.coagronet.tipoaplicacion.TipoAplicacion;
import com.fasterxml.jackson.annotation.JsonFilter;

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

/**
 * Representa la entidad de persistencia para la gestión de Subsistemas dentro
 * del dominio de aplicación.
 * <p>
 * Esta clase mapea la tabla <code>subsistema</code> del esquema público y
 * define la estructura
 * de datos para los módulos funcionales de la plataforma Coagronet. Implementa
 * la interfaz
 * {@link Serializable} para garantizar la persistencia y transferencia de
 * objetos a través de la red.
 * </p>
 * <p>
 * Utiliza anotaciones de <strong>Lombok</strong> para la generación automática
 * de código repetitivo
 * (<i>boilerplate</i>) y <strong>JPA</strong> para el mapeo objeto-relacional.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @since 2025
 */
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
@JsonFilter("filtroDinamico")
public class SubSistema implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único (Llave Primaria) del subsistema.
     * <p>
     * Este valor es generado automáticamente mediante una secuencia de base de
     * datos
     * (<code>subsistema_sub_id_seq</code>).
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUB_SEQ")
    @Column(name = "sub_id")
    private Long id;

    /**
     * Nombre descriptivo del subsistema.
     * <p>
     * Representa la etiqueta funcional que identificará al módulo en la interfaz de
     * usuario.
     * Es un campo obligatorio con una longitud máxima de 100 caracteres.
     * </p>
     */
    @Column(name = "sub_nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Identificador o ruta del icono asociado al subsistema.
     * <p>
     * Se utiliza para la representación gráfica del módulo en el menú o dashboard.
     * </p>
     */
    @Column(name = "sub_icon", nullable = false, length = 255)
    private String icon;

    /**
     * Estado actual del ciclo de vida del subsistema.
     * <p>
     * Relación de cardinalidad "muchos a uno" ({@code @ManyToOne}) con la entidad
     * {@link Estado}.
     * Configurada con carga perezosa (<i>Lazy Loading</i>) para optimizar el
     * rendimiento.
     * Este campo es obligatorio para garantizar la integridad referencial.
     * </p>
     * 
     * @see Estado
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sub_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "subsistema_sub_estado_id_fkey"))
    private Estado estado;

    /**
     * Clasificación del tipo de aplicación al que pertenece el subsistema.
     * <p>
     * Relación opcional "muchos a uno" con la entidad {@link TipoAplicacion}.
     * Permite categorizar el subsistema (ej. Web, Móvil, Servicio).
     * </p>
     * 
     * @see TipoAplicacion
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "sub_tipo_aplicacion_id", referencedColumnName = "tia_id", nullable = true, foreignKey = @ForeignKey(name = "subsistema_sub_tipo_aplicacion_id_fkey"))
    private TipoAplicacion tipoAplicacion;

}