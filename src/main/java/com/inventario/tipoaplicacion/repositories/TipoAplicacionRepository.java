package com.inventario.tipoaplicacion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.tipoaplicacion.TipoAplicacion;

/**
 * Interfaz de repositorio encargada de la persistencia y gestión de datos para
 * la entidad {@link TipoAplicacion}.
 * <p>
 * Extiende de {@link JpaRepository} para proporcionar operaciones CRUD (Crear,
 * Leer, Actualizar, Borrar) estándar
 * y funcionalidades de paginación listas para usar. Actúa como el componente de
 * acceso a datos para el catálogo
 * maestro de plataformas o entornos de despliegue del sistema.
 * </p>
 *
 * @author jujcgu
 * @version 1.0
 * @see JpaRepository
 * @see TipoAplicacion
 * @since 2026
 */
public interface TipoAplicacionRepository extends JpaRepository<TipoAplicacion, Long> {

}
