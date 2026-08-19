package com.inventario.tipoFase.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.tipoFase.TipoFase;

@Repository
public interface TipoFaseRepository extends JpaRepository<TipoFase, Long>{
    Optional<TipoFase> findByIdAndEmpresaId(Long id, Long empresaId);

    List<TipoFase> findByEmpresaIdOrderByIdAsc(Long empresaId);

    List<TipoFase> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);
    
}
