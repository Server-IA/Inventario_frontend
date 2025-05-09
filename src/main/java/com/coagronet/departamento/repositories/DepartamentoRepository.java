package com.coagronet.departamento.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.departamento.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

        Optional<Departamento> findByIdAndEmpresaId(
                        Long id,
                        Long empresaId);

        List<Departamento> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(
                        Long empresaId,
                        Long estadoId);

        List<Departamento> findByEmpresaIdOrderByIdAsc(
                        Long empresaId);

        boolean existsByIdAndEmpresaId(
                        Long id,
                        Long empresaId);

        boolean existsByIdAndEstadoIdNot(
                        Long id,
                        Long estadoId);

}
