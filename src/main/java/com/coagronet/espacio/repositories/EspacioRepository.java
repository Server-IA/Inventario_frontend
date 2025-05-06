package com.coagronet.espacio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.espacio.Espacio;

public interface EspacioRepository extends JpaRepository<Espacio, Long> {


    List<Espacio> findByBloqueSedeEmpresaIdAndBloqueIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Integer bloqueId,
            Integer estadoId);

    Optional<Espacio> findByIdAndBloqueSedeEmpresaIdAndEstadoIdNot(Long id, Long empresaId, Integer estadoId);

    boolean existsByIdAndBloqueSedeEmpresaIdAndEstadoIdNot(Long id, Long empresaId, Integer estadoId);
}
