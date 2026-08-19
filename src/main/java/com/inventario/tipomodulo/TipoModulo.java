package com.inventario.tipomodulo;

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
 * Representa la entidad de catálogo que define la clasificación funcional o
 * técnica de los módulos del sistema.
 * <p>
 * Esta clase mapea la tabla <code>tipo_modulo</code> y se utiliza para agrupar
 * los componentes de software
 * según su naturaleza (ej. "Reporte", "Formulario Transaccional", "Dashboard").
 * Actúa como una tabla paramétrica
 * maestra en el modelo relacional.
 * </p>
 * <p>
 * Implementa la interfaz {@link Serializable} para soportar la transferencia de
 * datos en arquitecturas distribuidas
 * y utiliza la anotación {@link JsonFilter} para permitir la serialización
 * condicional de sus atributos en las respuestas REST.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see com.inventario.subsistema.Modulo
 * @since 2025
 */
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
@JsonFilter("filtroDinamico")
public class TipoModulo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único (Llave Primaria) del tipo de módulo.
     * <p>
     * Este valor es gestionado automáticamente por la secuencia de base de datos
     * <code>tipo_modulo_tim_id_seq</code>.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TIM_SEQ")
    @Column(name = "tim_id")
    private Long id;

    /**
     * Nombre descriptivo de la categoría.
     * <p>
     * Etiqueta corta utilizada para listar o filtrar los tipos de módulos en la
     * interfaz de usuario.
     * Es un campo obligatorio con restricción de longitud a 100 caracteres.
     * </p>
     */
    @Column(name = "tim_nombre", nullable = false, length = 100)
    private String nombre;

    /**
     * Detalle extendido sobre el propósito de este tipo de módulo.
     * <p>
     * Permite almacenar una explicación técnica o funcional de hasta 2048
     * caracteres, útil para
     * la documentación en línea o guías de administración del sistema.
     * </p>
     */
    @Column(name = "tim_descripcion", length = 2048)
    private String descripcion;

    /**
     * Estado de vigencia del tipo de módulo en el sistema.
     * <p>
     * Relación "muchos a uno" con la entidad {@link Estado}. Se utiliza para
     * realizar borrado lógico
     * o desactivación temporal de categorías sin eliminar el registro físico.
     * Configurada con {@code FetchType.LAZY} para optimizar el consumo de memoria.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tim_estado_id", referencedColumnName = "est_id", nullable = false, foreignKey = @ForeignKey(name = "tipo_modulo_tim_estado_id_fkey"))
    private Estado estado;

}
