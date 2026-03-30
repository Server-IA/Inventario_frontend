package com.coagronet.usuariorol.repositories;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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
			select ur from UsuarioRol ur
			where ur.deletedAt is null
			and ur.estado.id = :estadoInactivo
			and ur.iniciaContratoEn <= :fechaActual
			and (ur.finalizaContratoEn is null or ur.finalizaContratoEn >= :fechaActual)
			""")
	List<UsuarioRol> findByEstadoInactivoYFechaActivacion(
			@Param("estadoInactivo") Long estadoInactivo,
			@Param("fechaActual") OffsetDateTime fechaActual);

	@Query("""
			select ur from UsuarioRol ur
			where ur.deletedAt is null
			and ur.estado.id = :estadoActivo
			and ur.finalizaContratoEn is not null
			and ur.finalizaContratoEn < :fechaActual
			""")
	List<UsuarioRol> findByEstadoActivoYFechaFinalizacionPasada(
			@Param("estadoActivo") Long estadoActivo,
			@Param("fechaActual") OffsetDateTime fechaActual);

	@Query("""
			select ur from UsuarioRol ur
			where ur.deletedAt is null
			and ur.estado.id = :estadoActivo
			and ur.user.id = :userId
			order by ur.id asc
			""")
	List<UsuarioRol> findActivasByUserId(@Param("estadoActivo") Long estadoActivo, @Param("userId") Long userId);

}
