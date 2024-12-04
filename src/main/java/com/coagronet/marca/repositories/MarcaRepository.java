package com.coagronet.marca.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.marca.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    List<Marca> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
            Long empresaId,
            Integer estadoId);

    Optional<Marca> findByIdAndEmpresaIdAndEstadoIdNot(
            Long id,
            Long empresaId,
            Integer estadoId);

    boolean existsByIdAndEmpresaIdAndEstadoIdNot(
            Long id,
            Long empresaId,
            Integer estadoId);

}
