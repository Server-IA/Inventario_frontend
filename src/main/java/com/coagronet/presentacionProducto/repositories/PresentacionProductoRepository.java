package com.coagronet.presentacionProducto.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.presentacionProducto.PresentacionProducto;

@Repository
public interface PresentacionProductoRepository extends JpaRepository<PresentacionProducto, Long> {

	Page<PresentacionProducto> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<PresentacionProducto> findByIdAndEmpresaId(Long id, Long empresaId);

	PresentacionProducto getReferenceByIdAndEmpresaId(Long id, Long empresaId);

	@Query("SELECT p FROM PresentacionProducto p WHERE p.id = :id AND p.estado.id = :estadoId")
	Optional<PresentacionProducto> findByIdInAndEstadoId(@Param("id") Long id, @Param("estadoId") Long estadoId);

	@Query("SELECT p FROM PresentacionProducto p WHERE p.id IN :ids AND p.estado.id = :estado")
	List<PresentacionProducto> findAllByIdInAndEstado(@Param("ids") Set<Long> ids, @Param("estado") Long estado);

}
