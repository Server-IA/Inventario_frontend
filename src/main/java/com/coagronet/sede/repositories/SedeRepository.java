package com.coagronet.sede.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.sede.Sede;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

	List<Sede> findByEmpresaId(Long empresaId);

	List<Sede> findByEmpresaIdAndEstadoIdNot(Long empresaId, Integer estadoId);

	Optional<Sede> findByIdAndEmpresaId(Long id, Long empresaId);
	
	Optional<Sede> findByIdAndEmpresaIdAndEstadoIdNot(Long id, Long empresaId, Integer estadoId);

	boolean existsByIdAndEmpresaIdAndEstadoIdNot(Long id, Long empresaId, Integer estadoId);

}
