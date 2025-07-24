package com.coagronet.tipoSede.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoSede.TipoSede;

public interface TipoSedeRepository extends JpaRepository<TipoSede, Long> {

	List<TipoSede> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

	List<TipoSede> findByEmpresaIdOrderByIdAsc(Long empresaId);

	Optional<TipoSede> findByIdAndEmpresaIdAndEstadoIdNot(Long id, Long empresaId, Long estadoId);

	Optional<TipoSede> findByIdAndEmpresaId(Long id, Long empresaId);

	boolean existsByIdAndEmpresaIdAndEstadoIdNot(Long id, Long empresaId, Long estadoId);

}
