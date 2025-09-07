package com.coagronet.seccion.repositories;

import com.coagronet.seccion.Seccion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {

	Optional<Seccion> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Seccion> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

}
