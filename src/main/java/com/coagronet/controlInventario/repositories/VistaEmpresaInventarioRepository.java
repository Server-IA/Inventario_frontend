package com.coagronet.controlInventario.repositories;

import com.coagronet.controlInventario.VistaEmpresaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VistaEmpresaInventarioRepository extends JpaRepository<VistaEmpresaInventario, Long> {

    List<VistaEmpresaInventario> findByInvEmpresaId(Long empresaId);
    List<VistaEmpresaInventario> findByInvEmpresaIdAndInvSubSeccionId(Long empresaId, Long subseccionId);
}
