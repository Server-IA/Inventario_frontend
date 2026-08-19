package com.inventario.tipoaplicacion;

import java.io.Serializable;

import com.inventario.estado.Estado;
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
 * Representa la entidad de catálogo que define las plataformas o entornos de
 * despliegue soportados por el sistema.
 * <p>
 * Esta clase mapea la tabla <code>tipo_aplicacion</code> y actúa como una tabla
 * maestra para categorizar
 * los módulos y subsistemas según su entorno de ejecución (ej. "Web", "Móvil
 * Android", "Escritorio").
 * </p>
 * <p>
 * Incorpora la anotación {@link JsonFilter} con el identificador
 * <code>filtroDinamico</code> para permitir
 * la proyección selectiva de atributos en las respuestas de la API, optimizando
 * el tráfico de red.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see com.inventario.subsistema.SubSistema
 * @see com.inventario.subsistema.Modulo
 * @since 2026
 */
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
@JsonFilter("filtroDinamico")
public class TipoAplicacion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único (Llave Primaria) del tipo de aplicación.
     * <p>
     * Valor numérico generado automáticamente mediante la secuencia de base de
     * datos <code>tipo_aplicacion_tia_id_seq</code>.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIA_SEQ")
    @Column(name = "tia_id")
    private Long id;

    /**
     * Nombre comercial o técnico de la plataforma.
     * <p>
     * Define la etiqueta principal utilizada en los selectores de la interfaz de
     * usuario.
     * Es un dato obligatorio con una longitud máxima de 100 caracteres.
     * </p>
     */
    @Column(name = "tia_nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Descripción detallada de las características del entorno.
     * <p>
     * Permite registrar información técnica adicional sobre la plataforma
     * (versiones soportadas, restricciones, etc.).
     * Campo opcional de gran capacidad (2048 caracteres).
     * </p>
     */
    @Column(name = "tia_descripcion", length = 2048)
    private String descripcion;

    /**
     * Estado operativo asociado al tipo de aplicación.
     * <p>
     * Relación "muchos a uno" con la entidad {@link Estado}. A diferencia de otros
     * catálogos, esta relación
     * está configurada como <strong>opcional</strong>
     * (<code>nullable = true</code>), permitiendo la existencia
     * de tipos de aplicación sin un estado explícito definido, aunque se recomienda
     * su asignación para mantener
     * la coherencia del ciclo de vida.
     * </p>
     * <p>
     * Utiliza carga perezosa (<code>FetchType.LAZY</code>) para evitar consultas
     * innecesarias a la tabla de estados.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "tia_estado_id", referencedColumnName = "est_id", nullable = true, foreignKey = @ForeignKey(name = "tipo_aplicacion_tia_estado_id_fkey"))
    private Estado estado;

}
