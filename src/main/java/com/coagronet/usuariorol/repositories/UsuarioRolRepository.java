package com.coagronet.usuariorol.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.user.User;
import com.coagronet.usuariorol.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

	Page<UsuarioRol> findAllByEmpresaId(Pageable pageable, Long empresaId);

	Optional<UsuarioRol> findByIdAndEmpresaId(Long id, Long empresaId);

	List<UsuarioRol> findByUserOrderByUserId(User user);

	UsuarioRol findByUser(User user);

	Optional<UsuarioRol> findByUserAndEmpresaIdAndRolId(User user, Long empresaId, Long rolId);

	boolean existsByUser_IdAndEmpresa_IdAndRol_IdAndEstado_IdAndFinalizaContratoEnIsNull(
			Long userId,
			Long empresaId,
			Long rolId,
			Long estadoId);

}
