package com.coagronet.proveedor.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.proveedor.Proveedor;

public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    boolean existsByIdAndEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);
}
