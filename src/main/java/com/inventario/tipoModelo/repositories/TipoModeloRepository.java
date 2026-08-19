package com.inventario.tipoModelo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.tipoModelo.TipoModelo;

@Repository
public interface TipoModeloRepository extends JpaRepository <TipoModelo, Long> {
    Optional<TipoModelo> findByIdAndEmpresaId (Long id, Long empresaId);
    List<TipoModelo> findByEmpresaIdOrderByIdAsc(Long empresaId);
    
}
