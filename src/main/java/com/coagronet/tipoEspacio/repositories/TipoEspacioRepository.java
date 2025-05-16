package com.coagronet.tipoEspacio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.tipoEspacio.TipoEspacio;

public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Long> {

	Optional<TipoEspacio> findByIdAndEmpresaId(Long id, Long empresaId);

	List<TipoEspacio> findByEmpresaIdOrderByIdAsc(Long empresaId);

	List<TipoEspacio> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}