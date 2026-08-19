package com.inventario.subsistema.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.inventario.subsistema.SubSistema;
import com.inventario.subsistema.repositories.SubSistemaRepository;

/**
 * Define la lógica de negocio y los servicios transaccionales para la gestión
 * de la entidad {@link SubSistema}.
 * <p>
 * Esta clase opera dentro de la capa de servicio (Service Layer) de la
 * arquitectura, actuando como intermediario
 * entre los controladores REST y la capa de persistencia. Su función principal
 * es encapsular las reglas de negocio
 * y coordinar el acceso a los datos a través de {@link SubSistemaRepository}.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see SubSistemaRepository
 * @see SubSistema
 * @since 2026
 */
@Service
public class SubSistemaService {

    private final SubSistemaRepository subSistemaRepository;

    /**
     * Crea una nueva instancia del servicio aplicando inyección de dependencias por
     * constructor.
     * <p>
     * Este mecanismo garantiza que el repositorio requerido esté disponible e
     * inmutable durante
     * el ciclo de vida del bean.
     * </p>
     *
     * @param subSistemaRepository interfaz de repositorio para el acceso a datos de
     *                             subsistemas.
     *                             Se espera que este componente sea gestionado por
     *                             el contenedor de Spring.
     */
    public SubSistemaService(SubSistemaRepository subSistemaRepository) {
        this.subSistemaRepository = subSistemaRepository;
    }

    /**
     * Recupera la totalidad de los subsistemas registrados en la base de datos.
     * <p>
     * Este método delega la consulta al repositorio JPA configurado, retornando
     * todas las instancias
     * persistidas sin aplicar paginación ni filtros adicionales.
     * </p>
     *
     * @return una lista de objetos {@link SubSistema} con todos los registros
     *         encontrados.
     *         Retorna una lista vacía si no existen datos, nunca retorna
     *         <code>null</code>.
     */
    public List<SubSistema> findAll() {
        return subSistemaRepository.findAll();
    }

}
