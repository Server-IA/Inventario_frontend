package com.coagronet.proveedor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.proveedor.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

    Optional<Proveedor> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Proveedor> findByEmpresaIdOrderByIdAsc(Long empresaId);
}
