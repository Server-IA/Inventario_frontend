package com.coagronet.sede.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.sede.Sede;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

	Optional<Sede> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Sede> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<Sede> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}
