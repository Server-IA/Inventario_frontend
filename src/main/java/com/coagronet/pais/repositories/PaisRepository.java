package com.coagronet.pais.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.pais.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

        Optional<Pais> findByIdAndEmpresaId(
                        Long id,
                        Long empresaId);

        List<Pais> findByEmpresaIdOrderByIdAsc(
                        Long empresaId);

        List<Pais> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
                        Long empresaId,
                        Long estadoId);

        boolean existsByIdAndEmpresaId(
                        Long id,
                        Long empresaId);

}
