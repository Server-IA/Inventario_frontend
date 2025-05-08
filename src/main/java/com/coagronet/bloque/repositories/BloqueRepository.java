package com.coagronet.bloque.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.bloque.Bloque;

public interface BloqueRepository extends JpaRepository<Bloque, Long> {

        List<Bloque> findBySedeIdAndEstadoIdNotAndSedeEmpresaIdOrderByIdAsc(
                        Long sedeId,
                        Long estadoId,
                        Long empresaId);

        Optional<Bloque> findByIdAndSedeEmpresaIdAndEstadoIdNot(
                        Long id,
                        Long empresaId,
                        Long estadoId);

        Optional<Bloque> findByIdAndSedeEmpresaId(
                        Long id,
                        Long empresaId);

        boolean existsByIdAndSedeEmpresaId(
                        Long id,
                        Long empresaId);

        boolean existsByIdAndSedeEmpresaIdAndEstadoIdNot(
                        Long id,
                        Long empresaId,
                        Long estadoId);
}
