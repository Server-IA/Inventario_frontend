package com.inventario.usuarioEstado.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.usuarioEstado.UsuarioEstado;

/**
 * Repositorio para la gestión de estados de usuario.
 *
 * @Repository indica al contenedor de Spring que traslade las excepciones nativas de
 * JPA/Hibernate a la jerarquía genérica DataAccessException de Spring.
 */
@Repository
public interface UsuarioEstadoRepository extends JpaRepository<UsuarioEstado, Long> {

}