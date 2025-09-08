package com.coagronet.presentacionProducto.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.presentacionProducto.PresentacionProducto;

import java.util.Optional;

@Repository
public interface PresentacionProductoRepository extends JpaRepository<PresentacionProducto, Long> {

	Page<PresentacionProducto> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<PresentacionProducto> findByIdAndEmpresaId(Long id, Long empresaId);

}
