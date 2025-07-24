package com.coagronet.espacioOcupacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.espacioOcupacion.EspacioOcupacion;

public interface EspacioOcupacionRepository extends JpaRepository<EspacioOcupacion, Long> {

	List<EspacioOcupacion> findByEmpresaIdOrderByIdAsc(Long empresaId);

	Optional<EspacioOcupacion> findByIdAndEmpresaId(Long id, Long empresaId);

}
