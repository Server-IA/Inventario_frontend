package com.coagronet.kardex.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.kardex.Kardex;

public interface KardexRepository extends JpaRepository<Kardex, Integer> {

    Optional<Kardex> findByIdAndAlmacenSedeEmpresaId(
            Integer id,
            Long empresaId);

    Page<Kardex> findByAlmacenSedeEmpresaIdAndEstadoIdNot(
            Long empresaId,
            Integer estadoId,
            Pageable pageable);

    boolean existsByIdAndAlmacenSedeEmpresaId(
            Integer id,
            Long empresaId);

}
