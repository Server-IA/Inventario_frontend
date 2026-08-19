package com.inventario.tipoMedicion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventario.tipoMedicion.TipoMedicion;

@Repository
public interface TipoMedicionRepository extends JpaRepository <TipoMedicion, Long>{
    Optional<TipoMedicion> findByIdAndEmpresaId (Long id, Long empresaId);
    List<TipoMedicion> findByEmpresaIdOrderByIdAsc(Long empresaId);
    List<TipoMedicion> findByEstadoId(Long estadoId);
    List<TipoMedicion> findByUnidadId(Long unidadId);
}
