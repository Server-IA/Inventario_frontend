package com.inventario.tipoaplicacion.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventario.tipoaplicacion.TipoAplicacion;
import com.inventario.tipoaplicacion.repositories.TipoAplicacionRepository;

/**
 * Provee la lógica de negocio y los servicios de lectura para la entidad
 * paramétrica {@link TipoAplicacion}.
 * <p>
 * Esta clase opera en la capa de servicio actuando como intermediario para la
 * gestión del catálogo de
 * plataformas o entornos de despliegue. Su función principal es centralizar el
 * acceso a los datos maestros
 * requeridos por los controladores REST y otros componentes del negocio.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see TipoAplicacionRepository
 * @see TipoAplicacion
 * @since 2026
 */
@Service
public class TipoAplicacionService {

    private final TipoAplicacionRepository tipoAplicacionRepository;

    /**
     * Construye la instancia del servicio inyectando el repositorio de persistencia
     * requerido.
     * <p>
     * Utiliza el patrón de inyección por constructor para asegurar que las
     * dependencias obligatorias
     * estén satisfechas al momento de instanciar el bean, favoreciendo la
     * inmutabilidad y facilitando
     * las pruebas unitarias.
     * </p>
     *
     * @param tipoAplicacionRepository interfaz de acceso a datos para la entidad
     *                                 TipoAplicacion.
     *                                 Gestionada automáticamente por el contenedor
     *                                 de Spring.
     */
    public TipoAplicacionService(TipoAplicacionRepository tipoAplicacionRepository) {
        this.tipoAplicacionRepository = tipoAplicacionRepository;
    }

    /**
     * Recupera el catálogo completo de tipos de aplicación disponibles en el
     * sistema.
     * <p>
     * Este método invoca al repositorio subyacente para obtener la totalidad de los
     * registros sin aplicar
     * paginación. Es utilizado frecuentemente para poblar listas desplegables
     * (combos) en la interfaz
     * de usuario o para validaciones de integridad en otros módulos.
     * </p>
     *
     * @return una lista de objetos {@link TipoAplicacion} que representa todas las
     *         plataformas soportadas.
     *         Retorna una lista vacía si no existen registros, garantizando que el
     *         resultado nunca sea <code>null</code>.
     */
    public List<TipoAplicacion> findAll() {
        return tipoAplicacionRepository.findAll();
    }

}
