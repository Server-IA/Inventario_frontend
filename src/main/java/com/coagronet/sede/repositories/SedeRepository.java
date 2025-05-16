package com.coagronet.sede.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.sede.Sede;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

	Optional<Sede> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Sede> findByEmpresaIdOrderByIdAsc(Long empresaId);

	List<Sede> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}
