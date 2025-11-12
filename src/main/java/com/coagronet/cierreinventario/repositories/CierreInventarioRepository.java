package com.coagronet.cierreinventario.repositories;

import com.coagronet.cierreinventario.CierreInventario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CierreInventarioRepository extends JpaRepository<CierreInventario, Long> {

    List<CierreInventario> findByEmpresaId(Long empresaId);
}
