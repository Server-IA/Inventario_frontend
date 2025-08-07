package com.coagronet.modelo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.modelo.Modelo;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Long>{
    
    Optional<Modelo> findByIdAndEmpresaId (Long id, Long empresaId);

    List<Modelo> findByEmpresaIdOrderByIdAsc (Long empresaId);

    List<Modelo> findByProductoIdOrderByIdAsc (Long empresaId);

    List<Modelo> findByTipoModeloIdOrderByIdAsc (Long tipoModeloId);
}
