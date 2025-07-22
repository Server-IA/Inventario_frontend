package com.coagronet.dispositivoMedicion.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.dispositivoMedicion.DispositivoMedicion;

@Repository
public interface DispositivoMedicionRepository extends JpaRepository <DispositivoMedicion, Long>{
    Optional <DispositivoMedicion> findByIdAndEmpresaId(Long id, Long empresaId);
    List <DispositivoMedicion> findByEmpresaIdOrderById(Long empresaId);
    List <DispositivoMedicion> findByEstadoId(Long estadoId);
}