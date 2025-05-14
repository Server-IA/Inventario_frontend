package com.coagronet.tipoBloque.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoBloque.TipoBloque;

@Repository
public interface TipoBloqueRepository extends JpaRepository<TipoBloque, Long> {

        List<TipoBloque> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
                        Long empresaId,
                        Integer estadoId);

        Optional<TipoBloque> findByIdAndEmpresaIdAndEstadoIdNot(
                        Long id,
                        Long empresaId,
                        Integer estadoId);

        Optional<TipoBloque> findByIdAndEmpresaId(
                Long id,
                Long empresaId);

        boolean existsByIdAndEmpresaIdAndEstadoIdNot(
                        Long id,
                        Long empresaId,
                        Integer estadoId);

}
