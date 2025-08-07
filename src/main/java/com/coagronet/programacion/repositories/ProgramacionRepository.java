package com.coagronet.programacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.programacion.Programacion;

@Repository
public interface ProgramacionRepository extends JpaRepository  <Programacion, Long>{
    Optional<Programacion> findByIdAndEmpresaId(Long id, Long empresaId);
    List<Programacion> findByEmpresaIdOrderByIdAsc(Long empresaId);
}
