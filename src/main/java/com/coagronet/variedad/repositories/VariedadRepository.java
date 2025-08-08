package com.coagronet.variedad.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.variedad.Variedad;

@Repository
public interface VariedadRepository extends JpaRepository<Variedad, Long> {
    Optional<Variedad> findByIdAndEmpresaId(Long id, Long empresaId);
    
    List<Variedad> findByEmpresaIdOrderByIdAsc(Long empresaId);

    

    List<Variedad> findByTipoProduccionId(Long tipoProduccionId);

    
}
