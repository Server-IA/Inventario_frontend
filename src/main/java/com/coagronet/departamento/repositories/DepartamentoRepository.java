package com.coagronet.departamento.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.departamento.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

	Optional<Departamento> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Departamento> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<Departamento> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}
