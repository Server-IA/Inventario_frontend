package com.coagronet.tipoSede.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoSede.TipoSede;

public interface TipoSedeRepository extends JpaRepository<TipoSede, Integer> {

    List<TipoSede> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long empresaId,
            Integer estadoId);

    Optional<TipoSede> findByIdAndEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEmpresaIdAndEstadoIdNot(Integer id,
            Long empresaId,
            Integer estadoId);

}
