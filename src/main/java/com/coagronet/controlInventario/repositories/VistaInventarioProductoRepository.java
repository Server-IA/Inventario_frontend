package com.coagronet.controlInventario.repositories;

import com.coagronet.controlInventario.VistaInventarioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VistaInventarioProductoRepository extends JpaRepository<VistaInventarioProducto, Long> {

    List<VistaInventarioProducto> findByProEmpresaId(Long empresaId);

}
