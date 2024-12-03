package com.coagronet.espacioOcupacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.espacioOcupacion.EspacioOcupacion;

public interface EspacioOcupacionRepository extends JpaRepository<EspacioOcupacion, Integer> {

    List<EspacioOcupacion> findByEspacioIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Integer espacioId,
            Long empresaId,
            Integer estadoId);

    Optional<EspacioOcupacion> findByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEspacioBloqueSedeEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

}
