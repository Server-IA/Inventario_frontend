package com.coagronet.espacio.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.espacio.Espacio;

public interface EspacioRepository extends JpaRepository<Espacio, Long> {

	Optional<Espacio> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Espacio> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<Espacio> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}
