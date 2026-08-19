package com.inventario.tipoDispositivo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.tipoDispositivo.TipoDispositivo;

@Repository
public interface TipoDispositivoRepository extends JpaRepository<TipoDispositivo, Long>{
    Optional<TipoDispositivo> findByIdAndEmpresaId(Long id, Long empresaId);
    List<TipoDispositivo> findByEmpresaIdOrderByIdAsc(Long empresaId);
    List<TipoDispositivo> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);
    
}
