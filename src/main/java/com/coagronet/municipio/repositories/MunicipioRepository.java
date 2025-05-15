package com.coagronet.municipio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.municipio.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

	Optional<Municipio> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Municipio> findByDepartamentoIdAndEmpresaIdOrderByIdAsc(Long departamentoId, Long empresaId);

	List<Municipio> findByDepartamentoIdAndEmpresaIdAndEstadoIdNotOrderByIdAsc(Long departamentoId, Long empresaId,
			Long estadoId);

	boolean existsByIdAndDepartamentoIdAndEmpresaId(Long id, Long departamentoId, Long empresaId);

}