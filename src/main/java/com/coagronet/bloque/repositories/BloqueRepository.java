package com.coagronet.bloque.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.bloque.Bloque;

public interface BloqueRepository extends JpaRepository<Bloque, Integer> {

    List<Bloque> findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(
            Long sedeId,
            Integer estadoId,
            Long empresaId);

    Optional<Bloque> findByIdAndSedeEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndSedeEmpresaIdAndEstadoIdNot(
            Integer id,
            Long empresaId,
            Integer estadoId);
}
