package com.coagronet.subSeccionDispositivo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.subSeccionDispositivo.SubseccionDispositivo;

@Repository
public interface SubseccionDispositivoRepository extends JpaRepository <SubseccionDispositivo, Long>{
    Optional<SubseccionDispositivo> findByIdAndEmpresaId(Long id, Long empresaId);
    List<SubseccionDispositivo> findByEmpresaIdOrderByIdAsc(Long empresaId);
    List<SubseccionDispositivo> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);
    
}
