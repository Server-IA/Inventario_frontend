package com.coagronet.modulo.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coagronet.estado.Estado;
import com.coagronet.estado.repositories.EstadoRepository;
import com.coagronet.exceptionHandler.custom.EntidadNoEncontradaException;
import com.coagronet.exceptionHandler.custom.RecursoDuplicadoException;
import com.coagronet.exceptionHandler.custom.RecursoNoEncontradoException;
import com.coagronet.modulo.Modulo;
import com.coagronet.modulo.dtos.ModuloDetailResponse;
import com.coagronet.modulo.dtos.ModuloRequest;
import com.coagronet.modulo.dtos.ModuloSummaryResponse;
import com.coagronet.modulo.repositories.ModuloRepository;
import com.coagronet.subsistema.SubSistema;
import com.coagronet.subsistema.repositories.SubSistemaRepository;
import com.coagronet.tipoaplicacion.TipoAplicacion;
import com.coagronet.tipoaplicacion.repositories.TipoAplicacionRepository;
import com.coagronet.tipomodulo.TipoModulo;
import com.coagronet.tipomodulo.repositories.TipoModuloRepository;

/**
 * Implementa la lógica de negocio y las reglas de validación para la gestión de módulos del sistema.
 * <p>
 * Esta clase orquesta la interacción entre las entidades de dominio ({@link Modulo}, {@link Estado},
 * {@link SubSistema}, etc.) y sus respectivos repositorios. Se encarga de garantizar la integridad referencial y la
 * unicidad de los registros antes de la persistencia.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see ModuloRepository
 * @see ModuloRequest
 * @since 2026
 */
@Service
public class ModuloService {

        private final ModuloRepository moduloRepository;
        private final EstadoRepository estadoRepository;
        private final SubSistemaRepository subSistemaRepository;
        private final TipoModuloRepository tipoModuloRepository;
        private final TipoAplicacionRepository tipoAplicacionRepository;

        /**
         * Construye el servicio inyectando todas las dependencias necesarias de repositorios.
         * <p>
         * La inyección por constructor asegura que el servicio no pueda ser instanciado en un estado inválido (sin
         * acceso a datos).
         * </p>
         *
         * @param moduloRepository repositorio para operaciones CRUD sobre la entidad Modulo.
         * @param estadoRepository repositorio para validar y recuperar el estado operativo.
         * @param subSistemaRepository repositorio para asociar el módulo a su subsistema padre.
         * @param tipoModuloRepository repositorio para clasificar el tipo de funcionalidad.
         * @param tipoAplicacionRepository repositorio para definir la plataforma de despliegue.
         */
        public ModuloService(ModuloRepository moduloRepository, EstadoRepository estadoRepository,
                        SubSistemaRepository subSistemaRepository, TipoModuloRepository tipoModuloRepository,
                        TipoAplicacionRepository tipoAplicacionRepository) {
                this.moduloRepository = moduloRepository;
                this.estadoRepository = estadoRepository;
                this.subSistemaRepository = subSistemaRepository;
                this.tipoModuloRepository = tipoModuloRepository;
                this.tipoAplicacionRepository = tipoAplicacionRepository;
        }

        /**
         * Registra un nuevo módulo en la base de datos tras validar sus dependencias y restricciones de negocio.
         * <p>
         * El proceso de creación sigue los siguientes pasos:
         * <ol>
         * <li>Verifica la unicidad del nombre comercial para evitar duplicados.</li>
         * <li>Resuelve las referencias a llaves foráneas (Estado, Subsistema, Tipos), lanzando excepción si alguna no
         * existe.</li>
         * <li>Transforma la lista de roles del DTO a un arreglo de cadenas compatible con el tipo de dato de
         * PostgreSQL.</li>
         * <li>Construye y persiste la entidad {@link Modulo}.</li>
         * </ol>
         * </p>
         *
         * @param request objeto de transferencia (DTO) con los datos de entrada validados previamente por el
         * controlador.
         * @return el identificador único (ID) del módulo recién creado.
         * @throws RecursoDuplicadoException si ya existe un módulo con el mismo nombre en el sistema.
         * @throws EntidadNoEncontradaException si alguno de los IDs relacionados (Estado, SubSistema, TipoModulo,
         * TipoAplicacion) no corresponde a un registro existente.
         * @see ModuloRequest
         */
        @Transactional
        public Long crearModulo(ModuloRequest request) {

                if (moduloRepository.existsByNombre(request.nombre())) {
                        throw new RecursoDuplicadoException(request.nombre());
                }

                Estado estado = estadoRepository.findById(request.estadoId())
                                .orElseThrow(() -> new EntidadNoEncontradaException("Estado", request.estadoId()));

                SubSistema subSistema = subSistemaRepository.findById(request.subSistemaId()).orElseThrow(
                                () -> new EntidadNoEncontradaException("SubSistema", request.subSistemaId()));

                TipoModulo tipoModulo = tipoModuloRepository.findById(request.tipoModuloId()).orElseThrow(
                                () -> new EntidadNoEncontradaException("Tipo de Módulo", request.tipoModuloId()));

                TipoAplicacion tipoAplicacion = tipoAplicacionRepository.findById(request.tipoAplicacionId())
                                .orElseThrow(() -> new EntidadNoEncontradaException("Tipo de Aplicación",
                                                request.tipoAplicacionId()));

                // Transformación de List<String> a String[] para compatibilidad con array de
                // PostgreSQL
                String[] rolesArray = (request.roles() != null && !request.roles().isEmpty())
                                ? request.roles().toArray(new String[0])
                                : null;

                Modulo modulo = Modulo.builder().nombre(request.nombre()).url(request.url())
                                .descripcion(request.descripcion()).icon(request.icon()).estado(estado)
                                .subSistema(subSistema).tipoModulo(tipoModulo).tipoAplicacion(tipoAplicacion)
                                .rolId(rolesArray).nombreId(request.nombreId()).requerido(request.requerido()).build();

                return moduloRepository.save(modulo).getId();
        }

        /**
         * Actualiza la información operativa y funcional de un módulo existente en el sistema.
         * <p>
         * Este método ejecuta una transacción atómica para modificar los atributos de la entidad {@link Modulo}. Antes
         * de aplicar los cambios, realiza un conjunto estricto de validaciones de integridad y reglas de negocio:
         * </p>
         * <ul>
         * <li>Confirma la existencia del registro objetivo en la base de datos.</li>
         * <li>Valida la unicidad del nombre, asegurando que no entre en conflicto con otros registros (excluyendo el
         * propio módulo en edición).</li>
         * <li>Verifica la validez de todas las llaves foráneas referenciadas (Estado, Subsistema, Tipos).</li>
         * </ul>
         * <p>
         * Adicionalmente, gestiona la transformación de tipos de datos, convirtiendo la lista de roles del DTO a un
         * arreglo de cadenas (<code>String[]</code>) para garantizar la compatibilidad con el tipo de columna
         * específico de PostgreSQL.
         * </p>
         *
         * @param id identificador único (llave primaria) del módulo que se desea actualizar.
         * @param request objeto de transferencia (DTO) que contiene los nuevos valores a persistir. No debe ser
         * <code>null</code>.
         * @throws RecursoNoEncontradoException si no existe un módulo asociado al <code>id</code> proporcionado.
         * @throws RecursoDuplicadoException si el nombre especificado en el <code>request</code> ya está asignado a
         * otro módulo diferente en el sistema.
         * @throws EntidadNoEncontradaException si alguna de las entidades relacionadas (Estado, SubSistema, TipoModulo,
         * TipoAplicacion) no se encuentra en los catálogos respectivos.
         * @see ModuloRequest
         * @see Modulo
         * @since 2026
         */
        @Transactional
        public void actualizarModulo(Long id, ModuloRequest request) {

                Modulo modulo = moduloRepository.findById(id)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Módulo", id));

                if (moduloRepository.existsByNombreAndIdNot(request.nombre(), id)) {
                        throw new RecursoDuplicadoException(request.nombre());
                }

                Estado estado = estadoRepository.findById(request.estadoId())
                                .orElseThrow(() -> new EntidadNoEncontradaException("Estado", request.estadoId()));

                SubSistema subSistema = subSistemaRepository.findById(request.subSistemaId()).orElseThrow(
                                () -> new EntidadNoEncontradaException("SubSistema", request.subSistemaId()));

                TipoModulo tipoModulo = tipoModuloRepository.findById(request.tipoModuloId()).orElseThrow(
                                () -> new EntidadNoEncontradaException("Tipo de Módulo", request.tipoModuloId()));

                TipoAplicacion tipoAplicacion = tipoAplicacionRepository.findById(request.tipoAplicacionId())
                                .orElseThrow(() -> new EntidadNoEncontradaException("Tipo de Aplicación",
                                                request.tipoAplicacionId()));

                // Transformación de List<String> a String[] para compatibilidad con array de
                // PostgreSQL
                String[] rolesArray = (request.roles() != null && !request.roles().isEmpty())
                                ? request.roles().toArray(new String[0])
                                : null;

                modulo.setNombre(request.nombre());
                modulo.setUrl(request.url());
                modulo.setDescripcion(request.descripcion());
                modulo.setIcon(request.icon());
                modulo.setEstado(estado);
                modulo.setSubSistema(subSistema);
                modulo.setTipoModulo(tipoModulo);
                modulo.setTipoAplicacion(tipoAplicacion);
                modulo.setRolId(rolesArray);
                modulo.setNombreId(request.nombreId());
                modulo.setRequerido(request.requerido());

                moduloRepository.save(modulo);
        }

        /**
         * Recupera el listado paginado de módulos con una proyección de datos optimizada para la lectura.
         * <p>
         * Este método actúa como una fachada de servicio para la consulta
         * {@link ModuloRepository#findAllProjected(Pageable)}. Su objetivo principal es proveer información al cliente
         * (Frontend) minimizando el tráfico de red, ya que retorna objetos {@link ModuloResponse} con las relaciones ya
         * resueltas (nombres en lugar de IDs), evitando la necesidad de múltiples consultas adicionales o la
         * serialización de entidades completas.
         * </p>
         *
         * @param pageable objeto que encapsula la información de paginación (número de página, tamaño) y ordenamiento
         * solicitada por el controlador.
         * @return una página ({@link Page}) de objetos {@link ModuloResponse}. Retorna una página vacía si no existen
         * registros, garantizando que el resultado nunca sea <code>null</code>.
         * @see ModuloResponse
         * @see ModuloRepository#findAllProjected(Pageable)
         * @since 2026
         */
        public Page<ModuloSummaryResponse> obtenerModulos(Pageable pageable) {
                return moduloRepository.findAllProjected(pageable);
        }

        public ModuloDetailResponse obtenerDetalleModulo(Long id) {
                return moduloRepository.findByIdProjected(id)
                                .orElseThrow(() -> new RecursoNoEncontradoException("Módulo", id));
        }

}
