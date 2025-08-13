package com.coagronet.producto.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.producto.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

	Optional<Producto> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Producto> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
