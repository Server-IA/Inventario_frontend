package com.coagronet.usuariorol.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.user.User;
import com.coagronet.usuariorol.UsuarioRol;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

	@Query("""
			select ur from UsuarioRol ur
			join fetch ur.user u
			join fetch u.persona p
			join fetch ur.rol r
			join fetch ur.empresa e
			where ur.id = :id
			""")
	Optional<UsuarioRol> findForEmailById(@Param("id") Long id);

	Page<UsuarioRol> findAllByEmpresaIdAndDeletedAtIsNullAndEstadoIdNotOrderByIdDesc(Pageable pageable, Long empresaId,
			Long estadoId);

	Optional<UsuarioRol> findByIdAndEmpresaIdAndDeletedAtIsNullAndEstadoIdNot(Long id, Long empresaId, Long estadoId);

	List<UsuarioRol> findByUserOrderByUserId(User user);

	UsuarioRol findByUser(User user);

	Optional<UsuarioRol> findByUserAndEmpresaIdAndRolIdAndDeletedAtIsNullAndEstadoIdNot(User user, Long empresaId,
			Long rolId, Long estadoId);

	boolean existsByUser_IdAndEmpresa_IdAndRol_IdAndEstado_IdAndFinalizaContratoEnIsNull(Long userId, Long empresaId,
			Long rolId, Long estadoId);

	Page<UsuarioRol> findByDeletedAtIsNullAndEstadoIdNotOrderByIdDesc(Pageable pageable, Long estadoId);

	Optional<UsuarioRol> findByIdAndDeletedAtIsNullAndEstadoIdNot(Long id, Long estadoId);

	@Query("""
			SELECT ur.user.id FROM UsuarioRol ur
			WHERE ur.user.id IN :userIds
			  AND ur.empresa.id = :empresaId
			  AND ur.estado.id = :estadoId
			  AND ur.finalizaContratoEn IS NULL
			""")
	Set<Long> findResponsablesValidos(@Param("userIds") Set<Long> userIds, @Param("empresaId") Long empresaId,
			@Param("estadoId") Long estadoId);

}
