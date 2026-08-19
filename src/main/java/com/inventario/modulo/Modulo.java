package com.inventario.modulo;

import java.io.Serializable;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.inventario.estado.Estado;
import com.inventario.modulo.enums.AlcanceModulo;
import com.inventario.subsistema.SubSistema;
import com.inventario.tipoaplicacion.TipoAplicacion;
import com.inventario.tipomodulo.TipoModulo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Representa la unidad funcional básica o "Módulo" dentro de la arquitectura del sistema.
 * <p>
 * Esta entidad mapea la tabla <code>modulo</code> y define los componentes navegables o ejecutables que componen un
 * {@link SubSistema}. Implementa reglas de negocio estructurales mediante restricciones de base de datos, garantizando
 * que no existan módulos homónimos dentro del mismo subsistema y tipo de aplicación.
 * </p>
 * <p>
 * Incluye soporte para tipos de datos avanzados de PostgreSQL, como arreglos de cadenas para la gestión de roles,
 * utilizando las extensiones de Hibernate 6.
 * </p>
 *
 * @author jujcgu
 * @version 2.0
 * @see SubSistema
 * @see TipoModulo
 * @since 2026
 */
@Entity @DynamicUpdate @Table(name = "modulo", schema = "public", uniqueConstraints = {
                @UniqueConstraint(name = "ux_nombre_subsistema_tipo_aplicacion", columnNames = { "mod_nombre",
                                "mod_subsistema_id",
                                "mod_tipo_aplicacion_id" }) }) @SequenceGenerator(name = "MOD_SEQ", sequenceName = "modulo_mod_id_seq", schema = "public", initialValue = 1, allocationSize = 1) @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @ToString(exclude = {
                                                "estado", "subSistema", "tipoModulo",
                                                "tipoAplicacion" }) @EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Modulo implements Serializable {

        private static final long serialVersionUID = 3L;

        /**
         * Identificador único (Llave Primaria) del módulo.
         * <p>
         * Generado secuencialmente por la base de datos (<code>modulo_mod_id_seq</code>). Se utiliza como criterio de
         * igualdad en los métodos {@code equals} y {@code hashCode}.
         * </p>
         */
        @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MOD_SEQ") @Column(name = "mod_id") @EqualsAndHashCode.Include
        private Long id;

        /**
         * Nombre comercial o funcional del módulo.
         * <p>
         * Este campo forma parte de la llave única compuesta <code>ux_nombre_subsistema_tipo_aplicacion</code>. Es
         * obligatorio y tiene una longitud máxima de 100 caracteres.
         * </p>
         */
        @Column(name = "mod_nombre", nullable = false, length = 100)
        private String nombre;

        /**
         * Ruta relativa o URL de acceso al módulo dentro de la aplicación cliente.
         * <p>
         * Define el punto de entrada para la navegación del usuario (ej. <code>/ventas/facturacion</code>).
         * </p>
         */
        @Column(name = "mod_url", nullable = false, length = 100)
        private String url;

        /**
         * Descripción detallada del propósito y alcance del módulo.
         * <p>
         * Campo opcional de gran longitud (hasta 2048 caracteres) destinado a documentación en línea o tooltips de
         * ayuda para el usuario final.
         * </p>
         */
        @Column(name = "mod_descripcion", length = 2048)
        private String descripcion;

        /**
         * Estado operativo actual del módulo.
         * <p>
         * Referencia obligatoria a la entidad {@link Estado}. Determina si el módulo está activo, inactivo o en
         * mantenimiento. Configurada con carga perezosa ({@code FetchType.LAZY}).
         * </p>
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "mod_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_estado_id_fkey"))
        private Estado estado;

        /**
         * Identificador del recurso gráfico asociado al módulo.
         * <p>
         * Cadena de texto que referencia una clase CSS (ej. FontAwesome) o la ruta de un activo estático.
         * </p>
         */
        @Column(name = "mod_icon", length = 255)
        private String icon;

        /**
         * Subsistema padre al cual pertenece este módulo.
         * <p>
         * Establece la jerarquía de navegación y agrupamiento lógico. Un módulo no puede existir sin estar asociado a
         * un subsistema (relación obligatoria).
         * </p>
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "mod_subsistema_id", referencedColumnName = "sub_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_subsistema_id_fkey"))
        private SubSistema subSistema;

        /**
         * Categorización técnica o funcional del módulo.
         * <p>
         * Referencia a {@link TipoModulo} para distinguir entre módulos de reporte, transaccionales, de configuración,
         * etc.
         * </p>
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "mod_tipo_modulo_id", referencedColumnName = "tim_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_tipo_modulo_id_fkey"))
        private TipoModulo tipoModulo;

        /**
         * Plataforma o tipo de aplicación en la que se despliega el módulo.
         * <p>
         * Permite reutilizar la estructura de módulos para diferentes entornos (Web, Móvil, Escritorio).
         * </p>
         */
        @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "mod_tipo_aplicacion_id", referencedColumnName = "tia_id", nullable = false, foreignKey = @ForeignKey(name = "modulo_mod_tipo_aplicacion_id_fkey"))
        private TipoAplicacion tipoAplicacion;

        /**
         * Identificador técnico para uso en el frontend (DOM ID).
         * <p>
         * Utilizado para pruebas automatizadas (E2E) o referencias específicas en el código cliente.
         * </p>
         */
        @Column(name = "mod_nombre_id", length = 255)
        private String nombreId;

        /**
         * Indicador de obligatoriedad del módulo.
         * <p>
         * Señala si el módulo es crítico para la operación del sistema y no debe ser desactivado o removido de la
         * configuración base.
         * </p>
         */
        @Column(name = "mod_requerido")
        private Boolean requerido;

        /**
         * Define el alcance operativo o de visibilidad del módulo.
         * <p>
         * Mapeado a un tipo ENUM nativo de PostgreSQL (alcance_modulo).
         * </p>
         */
        @Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(name = "alcance")
        private AlcanceModulo alcance;

}
