package com.coagronet.tipoEspacio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoEspacio.TipoEspacio;

public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Integer> {

    List<TipoEspacio> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long empresaId,
            Integer estadoId);

    Optional<TipoEspacio> findByIdAndEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEmpresaIdAndEstadoIdNot(Integer id,
            Long empresaId,
            Integer estadoId);

}
