package com.coagronet.modulo.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.modulo.Modulo;

/**
 * Interfaz de repositorio encargada de la persistencia y gestión de datos para
 * la entidad {@link Modulo}.
 * <p>
 * Extiende de {@link JpaRepository} para proporcionar operaciones CRUD estándar
 * y define consultas
 * específicas de dominio utilizando tanto JPQL (Java Persistence Query
 * Language) como métodos
 * derivados de nombres (Query Methods).
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see JpaRepository
 * @see Modulo
 * @since 2026
 */
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    /**
     * Recupera una proyección de los datos de un módulo filtrando por su estado y
     * tipo de aplicación.
     * <p>
     * Ejecuta una consulta JPQL personalizada que selecciona atributos específicos
     * (nombreId, nombre, url, icon)
     * para optimizar la lectura de datos. Los resultados se ordenan de forma
     * descendente por el identificador
     * del módulo.
     * </p>
     *
     * @param estadoId         identificador de la llave foránea del
     *                         {@link com.coagronet.estado.Estado}.
     * @param tipoAplicacionId identificador de la llave foránea del
     *                         {@link com.coagronet.tipoaplicacion.TipoAplicacion}.
     * @return una instancia de {@link Modulo} con los datos recuperados que cumplen
     *         con los criterios de filtrado.
     *         Retorna <code>null</code> si no se encuentra coincidencia.
     */
    @Query("select m.nombreId, m.nombre, m.url, m.icon from Modulo m where m.estado.id = :estadoId and m.tipoAplicacion.id = :tipoAplicacionId order by m.id desc")
    Modulo findByEstadoIdAndTipoAplicacionId(@Param("estadoId") Long estadoId,
            @Param("tipoAplicacionId") Long tipoAplicacionId);

    /**
     * Obtiene un listado de módulos cuyos identificadores técnicos coinciden con
     * los valores suministrados.
     * <p>
     * Genera una consulta SQL con cláusula <code>IN</code> sobre la columna
     * <code>mod_nombre_id</code>,
     * permitiendo la recuperación masiva de registros basada en sus identificadores
     * de frontend (DOM IDs).
     * </p>
     *
     * @param nombreIds lista de cadenas de texto con los identificadores técnicos a
     *                  buscar.
     * @return una lista de objetos {@link Modulo} encontrados. Retorna una lista
     *         vacía si ninguno coincide.
     */
    List<Modulo> findByNombreIdIn(List<String> nombreIds);

    /**
     * Verifica la existencia de un registro persistido que coincida con el nombre
     * comercial especificado.
     * <p>
     * Este método derivado genera una consulta optimizada (generalmente
     * <code>SELECT 1...</code>) que
     * comprueba la presencia del dato sin necesidad de hidratar la entidad completa
     * en memoria.
     * Útil para validaciones previas a la inserción para evitar excepciones de
     * unicidad.
     * </p>
     *
     * @param nombre cadena de texto con el nombre del módulo a verificar.
     * @return <code>true</code> si existe al menos un módulo con ese nombre;
     *         <code>false</code> en caso contrario.
     */
    Boolean existsByNombre(String nombre);

}
