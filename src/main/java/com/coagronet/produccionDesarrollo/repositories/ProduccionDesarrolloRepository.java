package com.coagronet.produccionDesarrollo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.produccionDesarrollo.ProduccionDesarrollo;

@Repository
public interface ProduccionDesarrolloRepository extends JpaRepository<ProduccionDesarrollo , Long>{
    Optional <ProduccionDesarrollo> findByIdAndEmpresaId (Long id, Long empresaId);
    
    List <ProduccionDesarrollo> findByEmpresaIdOrderByIdAsc (Long empresaId);

    List <ProduccionDesarrollo> findByTipoMedicionId (Long tipoMedicionId);

    List <ProduccionDesarrollo> findByVariedadId (Long variedadId);

    List <ProduccionDesarrollo> findByFaseId (Long faseId);

    List <ProduccionDesarrollo> findByEstadoId (Long estadoId);
    
}
