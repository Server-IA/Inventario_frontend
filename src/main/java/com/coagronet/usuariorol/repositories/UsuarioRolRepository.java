package com.coagronet.usuariorol.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.user.User;
import com.coagronet.usuariorol.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

	Page<UsuarioRol> findAllByEmpresaIdAndDeletedAtIsNullAndEstadoIdNotOrderByIdDesc(Pageable pageable, Long empresaId,
			Long estadoId);

	Optional<UsuarioRol> findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(Long id, Long empresaId, Long estadoId);

	List<UsuarioRol> findByUserOrderByUserId(User user);

	UsuarioRol findByUser(User user);

	Optional<UsuarioRol> findByUserAndEmpresaIdAndRolIdAndDeletedAtIsNullOrEstadoIdNot(User user, Long empresaId,
			Long rolId, Long estadoId);

	boolean existsByUser_IdAndEmpresa_IdAndRol_IdAndEstado_IdAndFinalizaContratoEnIsNull(
			Long userId,
			Long empresaId,
			Long rolId,
			Long estadoId);

	Page<UsuarioRol> findByDeletedAtIsNullAndEstadoIdNotOrderByIdDesc(Pageable pageable, Long estadoId);

	Optional<UsuarioRol> findByIdAndDeletedAtIsNullAndEstadoIdNot(Long id, Long estadoId);

}
