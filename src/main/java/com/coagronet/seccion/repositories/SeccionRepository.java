package com.coagronet.seccion.repositories;

import com.coagronet.seccion.Seccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeccionRepository extends JpaRepository<Seccion, Long> {

    Optional<Seccion> findByIdAndEmpresaId(Long id, Long empresaId);

    List<Seccion> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
