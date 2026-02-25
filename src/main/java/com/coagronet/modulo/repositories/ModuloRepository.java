package com.coagronet.modulo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.modulo.Modulo;
import com.coagronet.modulo.dtos.ModuloDetailResponse;
import com.coagronet.modulo.dtos.ModuloSummaryResponse;
import com.coagronet.modulo.enums.AlcanceModulo;

/**
 * Interfaz de repositorio encargada de la persistencia y gestión de datos para la entidad {@link Modulo}.
 * <p>
 * Extiende de {@link JpaRepository} para proporcionar operaciones CRUD estándar y define consultas específicas de
 * dominio utilizando tanto JPQL (Java Persistence Query Language) como métodos derivados de nombres (Query Methods).
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
     * Recupera una proyección de los datos de un módulo filtrando por su estado y tipo de aplicación.
     * <p>
     * Ejecuta una consulta JPQL personalizada que selecciona atributos específicos (nombreId, nombre, url, icon) para
     * optimizar la lectura de datos. Los resultados se ordenan de forma descendente por el identificador del módulo.
     * </p>
     *
     * @param estadoId identificador de la llave foránea del {@link com.coagronet.estado.Estado}.
     * @param tipoAplicacionId identificador de la llave foránea del
     * {@link com.coagronet.tipoaplicacion.TipoAplicacion}.
     * @return una instancia de {@link Modulo} con los datos recuperados que cumplen con los criterios de filtrado.
     * Retorna <code>null</code> si no se encuentra coincidencia.
     */
    @Query("select m.nombreId, m.nombre, m.url, m.icon from Modulo m where m.estado.id = :estadoId and m.tipoAplicacion.id = :tipoAplicacionId order by m.id desc")
    Modulo findByEstadoIdAndTipoAplicacionId(@Param("estadoId") Long estadoId,
            @Param("tipoAplicacionId") Long tipoAplicacionId);

    /**
     * Obtiene un listado de módulos cuyos identificadores técnicos coinciden con los valores suministrados.
     * <p>
     * Genera una consulta SQL con cláusula <code>IN</code> sobre la columna <code>mod_nombre_id</code>, permitiendo la
     * recuperación masiva de registros basada en sus identificadores de frontend (DOM IDs).
     * </p>
     *
     * @param nombreIds lista de cadenas de texto con los identificadores técnicos a buscar.
     * @return una lista de objetos {@link Modulo} encontrados. Retorna una lista vacía si ninguno coincide.
     */
    List<Modulo> findByNombreIdIn(List<String> nombreIds);

    /**
     * Verifica la existencia de un registro persistido que coincida con el nombre comercial especificado.
     * <p>
     * Este método derivado genera una consulta optimizada (generalmente <code>SELECT 1...</code>) que comprueba la
     * presencia del dato sin necesidad de hidratar la entidad completa en memoria. Útil para validaciones previas a la
     * inserción para evitar excepciones de unicidad.
     * </p>
     *
     * @param nombre cadena de texto con el nombre del módulo a verificar.
     * @return <code>true</code> si existe al menos un módulo con ese nombre; <code>false</code> en caso contrario.
     */
    Boolean existsByNombre(String nombre);

    /**
     * Verifica si existe algún otro registro con el nombre especificado, excluyendo el identificador proporcionado.
     * <p>
     * Este método derivado (Query Method) es fundamental para las validaciones de integridad durante los procesos de
     * <strong>actualización</strong>. Permite comprobar que el nuevo nombre asignado no entre en conflicto con otros
     * registros existentes en la base de datos, ignorando el propio registro que está siendo modificado.
     * </p>
     * <p>
     * La consulta generada equivale lógicamente a:
     * <code>SELECT COUNT(*) > 0 FROM table WHERE nombre = ? AND id <> ?</code>.
     * </p>
     *
     * @param nombre cadena de caracteres con el nombre comercial o funcional que se desea validar.
     * @param id identificador único (llave primaria) del registro actual que debe ser omitido en la búsqueda para
     * evitar falsos positivos de duplicidad.
     * @return <code>true</code> si existe <strong>otro</strong> registro diferente que ya utiliza ese nombre;
     * <code>false</code> si el nombre está libre o solo pertenece al registro con el <code>id</code> dado.
     */
    boolean existsByNombreAndIdNot(String nombre, Long id);

    /**
     * Recupera una vista paginada y optimizada del catálogo completo de módulos.
     * <p>
     * Ejecuta una consulta de proyección JPQL que selecciona campos específicos y resuelve las relaciones (como nombres
     * de estado, subsistema y tipos) directamente en la base de datos. Los resultados son mapeados automáticamente al
     * constructor del record {@link ModuloResponse}.
     * </p>
     * <p>
     * Esta estrategia evita la carga de las entidades completas ({@link Modulo}) y sus asociaciones perezosas,
     * mejorando significativamente el rendimiento en listados masivos o reportes de interfaz de usuario.
     * </p>
     *
     * @param pageable objeto de la interfaz {@link Pageable} que encapsula la información de paginación (número de
     * página, tamaño) y criterios de ordenamiento solicitados por el cliente.
     * @return una página ({@link Page}) conteniendo los objetos {@link ModuloResponse} proyectados. Nunca retorna
     * <code>null</code>, sino una página vacía si no hay registros.
     * @see ModuloResponse
     * @see Pageable
     */
    @Query("select m.id, m.nombre, m.url, m.descripcion, m.icon, m.estado.nombre, m.subSistema.nombre, m.tipoModulo.nombre, m.tipoAplicacion.nombre, m.nombreId, m.requerido from Modulo m")
    Page<ModuloSummaryResponse> findAllProjected(Pageable pageable);

    /**
     * Recupera los datos puntuales de un módulo específico utilizando una proyección de lectura optimizada.
     * <p>
     * Ejecuta una consulta JPQL explícita para seleccionar únicamente los atributos definidos en el registro
     * {@link ModuloDetailResponse}. Esta operación está diseñada específicamente para soportar procesos de edición o
     * auditoría, ya que recupera los identificadores (IDs) de las relaciones (Estado, Subsistema, Tipos) en lugar de
     * sus descripciones, facilitando el enlace de datos en formularios del cliente.
     * </p>
     *
     * @param id identificador único (llave primaria) del módulo que se desea consultar.
     * @return un contenedor {@link Optional} que incluye la proyección detallada si el registro existe, o un
     * {@link Optional#empty()} si no se encuentra ningún módulo asociado al <code>id</code> provisto.
     * @see ModuloDetailResponse
     * @see Optional
     * @since 2026
     */
    @Query("select m.nombre, m.url, m.descripcion, m.icon, m.estado.id, m.subSistema.id, m.tipoModulo.id, m.tipoAplicacion.id, m.nombreId, m.requerido from Modulo m where m.id = :id")
    Optional<ModuloDetailResponse> findByIdProjected(@Param("id") Long id);

    /**
     * Recupera una entidad Modulo por su identificador, optimizando la carga de relaciones asociadas.
     * <p>
     * Sobrescribe el método estándar para aplicar un {@link EntityGraph}. Esto fuerza una carga ansiosa (eager
     * fetching) de las propiedades "estado", "subSistema", "tipoModulo" y "tipoAplicacion" en una sola consulta,
     * evitando problemas de rendimiento n+1 al serializar la respuesta.
     * </p>
     *
     * @param id El identificador único de la entidad buscada.
     * @return Un {@link Optional} que contiene el módulo si existe, o vacío si no.
     */
    @Override @EntityGraph(attributePaths = { "estado", "subSistema", "tipoModulo", "tipoAplicacion" })
    Optional<Modulo> findById(Long id);

    // Busca módulos de la empresa que son requeridos obligatoriamente
    List<Modulo> findByAlcanceAndRequeridoTrue(AlcanceModulo alcance);

}
