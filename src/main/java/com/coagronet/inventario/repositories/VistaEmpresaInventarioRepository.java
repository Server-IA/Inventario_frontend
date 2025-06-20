package com.coagronet.inventario.repositories;

import com.coagronet.inventario.VistaEmpresaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VistaEmpresaInventarioRepository extends JpaRepository<VistaEmpresaInventario, Long> {

    List<VistaEmpresaInventario> findByInvEmpresaId(Long empresaId);
}
