package com.coagronet.usuariorol.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.user.User;
import com.coagronet.usuariorol.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

	List<UsuarioRol> findByUserOrderByUserId(User user);

	UsuarioRol findByUser(User user);

	Optional<UsuarioRol> findByUserAndEmpresaIdAndRolId(User user, Long empresaId, Long rolId);

}
