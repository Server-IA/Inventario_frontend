package com.coagronet.ocupacion.repositories;

import com.coagronet.ocupacion.Ocupacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OcupacionRepository extends JpaRepository<Ocupacion, Long> {

	Optional<Ocupacion> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Ocupacion> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
