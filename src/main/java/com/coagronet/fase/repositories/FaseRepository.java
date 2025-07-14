package com.coagronet.fase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.fase.Fase;

@Repository
public interface FaseRepository extends JpaRepository<Fase, Long>{
    Optional<Fase> findByIdAndEmpresaId(Long id, Long empresaId);
    List<Fase> findByEmpresaIdOrderByIdAsc(Long empresaId);
    List<Fase> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);
    

    

    
}