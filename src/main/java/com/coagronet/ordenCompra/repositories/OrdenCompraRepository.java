package com.coagronet.ordenCompra.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.ordenCompra.OrdenCompra;

public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

	List<OrdenCompra> findByEmpresaIdOrderByIdAsc(Long empresaId);

	Optional<OrdenCompra> findByIdAndEmpresaId(Long id, Long empresaId);

}
