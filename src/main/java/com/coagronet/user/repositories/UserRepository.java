package com.coagronet.user.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.user.User;
import com.coagronet.usuarioEstado.UsuarioEstado;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Page<User> findByUsuarioEstadoIdGreaterThanEqual(int usuarioEstadoId, Pageable pageable);

	Optional<User> findById(Long id);

	Boolean existsByUsername(String username);

	@Query("""
			    SELECT u FROM User u
			    JOIN FETCH u.usuarioEstado
			    LEFT JOIN FETCH u.rolesAsignados ur
			    LEFT JOIN FETCH ur.rol
			    LEFT JOIN FETCH ur.estado
			    WHERE u.username = :username
			""")
	Optional<User> findByUsernameWithRolesAndEstado(@Param("username") String username);

	boolean existsByUsernameAndUsuarioEstado(String email, UsuarioEstado estado);

}
