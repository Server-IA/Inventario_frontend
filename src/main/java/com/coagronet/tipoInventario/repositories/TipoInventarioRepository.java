package com.coagronet.tipoInventario.repositories;

import com.coagronet.tipoInventario.TipoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TipoInventarioRepository extends JpaRepository<TipoInventario, Long> {

    Optional<TipoInventario> findByIdAndEmpresaId(Long id, Long empresaId);
    List<TipoInventario> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
