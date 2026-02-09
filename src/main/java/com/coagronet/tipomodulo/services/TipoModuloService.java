package com.coagronet.tipomodulo.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.coagronet.tipomodulo.TipoModulo;
import com.coagronet.tipomodulo.repositories.TipoModuloRepository;

/**
 * Provee la lógica de negocio y los servicios de lectura para la entidad
 * paramétrica {@link TipoModulo}.
 * <p>
 * Esta clase opera en la capa de servicio actuando como intermediario para la
 * gestión de datos maestros
 * o catálogos del sistema. Su función principal es exponer los tipos de módulos
 * disponibles para ser
 * consumidos por componentes de interfaz de usuario (ej. listas desplegables) o
 * validaciones de negocio.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see TipoModuloRepository
 * @see TipoModulo
 * @since 2026
 */
@Service
public class TipoModuloService {

    private final TipoModuloRepository tipoModuloRepository;

    /**
     * Construye la instancia del servicio inyectando el repositorio de persistencia
     * requerido.
     * <p>
     * Aplica el patrón de inyección por constructor para garantizar la
     * inmutabilidad de las dependencias
     * y facilitar la creación de pruebas unitarias (mocking).
     * </p>
     *
     * @param tipoModuloRepository interfaz de acceso a datos para la entidad
     *                             TipoModulo.
     *                             Gestionada por el contenedor de Spring.
     */
    public TipoModuloService(TipoModuloRepository tipoModuloRepository) {
        this.tipoModuloRepository = tipoModuloRepository;
    }

    /**
     * Recupera el catálogo completo de tipos de módulo registrados en el sistema.
     * <p>
     * Este método delega la consulta al repositorio subyacente para obtener todos
     * los registros sin aplicar
     * filtros de paginación. Es ideal para la carga de selectores en el frontend o
     * procesos de configuración
     * que requieren la totalidad de las opciones disponibles.
     * </p>
     *
     * @return una lista de objetos {@link TipoModulo} con la totalidad de registros
     *         existentes.
     *         Retorna una lista vacía si no hay datos, nunca retorna
     *         <code>null</code>.
     */
    public List<TipoModulo> findAll() {
        return tipoModuloRepository.findAll();
    }

}
