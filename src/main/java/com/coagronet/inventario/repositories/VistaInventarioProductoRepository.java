package com.coagronet.inventario.repositories;

import com.coagronet.inventario.VistaInventarioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VistaInventarioProductoRepository extends JpaRepository<VistaInventarioProducto, Long> {

    List<VistaInventarioProducto> findByProEmpresaId(Long empresaId);

}
