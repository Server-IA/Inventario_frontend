package com.coagronet.producto.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.producto.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    Optional<Producto> findByIdAndEmpresaId(
            Integer id,
            Long empresaId);

    Page<Producto> findByEmpresaIdAndEstadoIdNot(
            Long empresaId,
            Integer estado,
            Pageable pageable);

    boolean existsByIdAndEmpresaId(
            Integer id,
            Long empresaId);

}
