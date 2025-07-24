package com.coagronet.presentacionProducto.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.presentacionProducto.PresentacionProducto;

import java.util.List;
import java.util.Optional;

@Repository
public interface PresentacionProductoRepository extends JpaRepository<PresentacionProducto, Long> {

	List<PresentacionProducto> findByEmpresaIdOrderByIdAsc(Long empresaId);

	Optional<PresentacionProducto> findByIdAndEmpresaId(Long id, Long empresaId);

}
