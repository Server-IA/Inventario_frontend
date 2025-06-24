package com.coagronet.subseccion.repositories;

import com.coagronet.subseccion.Subseccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubseccionRepository extends JpaRepository<Subseccion, Long> {

    List<Subseccion> findByEmpresaIdOrderByIdAsc(Long empresaId);

    Optional<Subseccion>findByIdAndEmpresaId(Long id, Long empresaId);
}
