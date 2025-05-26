package com.coagronet.proceso.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.proceso.Proceso;

@Repository
public interface ProcesoRepository extends JpaRepository<Proceso, Long> {

	Optional<Proceso> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Proceso> findByEmpresaIdOrderByIdAsc(Long empresaId);

}
