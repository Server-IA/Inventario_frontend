package com.coagronet.espacioOcupacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.espacioOcupacion.EspacioOcupacion;

public interface EspacioOcupacionRepository extends JpaRepository<EspacioOcupacion, Long> {

    List<EspacioOcupacion> findByEspacioIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long espacioId,
            Long empresaId,
            Integer estadoId);

    Optional<EspacioOcupacion> findByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(
            Long id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(
            Long id,
            Long empresaId,
            Integer estadoId);

}
