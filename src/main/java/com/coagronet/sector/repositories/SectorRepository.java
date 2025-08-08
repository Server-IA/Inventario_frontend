package com.coagronet.sector.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.sector.Sector;



@Repository
public interface SectorRepository extends JpaRepository <Sector, Long> {
    
    Optional <Sector> findByIdAndEmpresaId (Long id, Long empresaId);
    
    List <Sector> findByEmpresaIdOrderByIdAsc (Long empresaId);

    List <Sector> findByVariedadId (Long variedadId);

    List <Sector> findBySubseccionId (Long subseccionId);

    List <Sector> findByEstadoId (Long estadoId);
}
