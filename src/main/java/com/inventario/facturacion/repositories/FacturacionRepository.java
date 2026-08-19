package com.inventario.facturacion.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.facturacion.Facturacion;

public interface FacturacionRepository extends JpaRepository<Facturacion, Long> {

	Page<Facturacion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<Facturacion> findByIdAndEmpresaId(Long id, Long empresaId);

}
