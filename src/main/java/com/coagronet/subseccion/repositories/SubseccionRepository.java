package com.coagronet.subseccion.repositories;

import com.coagronet.subseccion.Subseccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubseccionRepository extends JpaRepository<Subseccion, Long> {

	Page<Subseccion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	Optional<Subseccion> findByIdAndEmpresaId(Long id, Long empresaId);

}
