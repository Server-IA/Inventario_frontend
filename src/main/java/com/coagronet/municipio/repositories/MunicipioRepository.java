package com.coagronet.municipio.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.municipio.Municipio;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

	List<Municipio> findByDepartamentoIdOrderByIdAsc(Long departamentoId);

	List<Municipio> findByDepartamentoIdAndEstadoIdNotOrderByIdAsc(Long departamentoId, Long estadoId);

	// Compatibilidad para modulos que todavia tienen el campo empresa antes de que
	// municipio se volviera global.
	default Optional<Municipio> findByIdAndEmpresaId(Long id, Long empresaId) {
		return findById(id);
	}

	boolean existsByIdAndDepartamentoId(Long id, Long departamentoId);

	boolean existsByDepartamentoIdAndNombreIgnoreCase(Long departamentoId, String nombre);

	boolean existsByDepartamentoIdAndNombreIgnoreCaseAndIdNot(Long departamentoId, String nombre, Long id);

	boolean existsByDepartamentoIdAndCodigo(Long departamentoId, Integer codigo);

	boolean existsByDepartamentoIdAndCodigoAndIdNot(Long departamentoId, Integer codigo, Long id);

	boolean existsByDepartamentoIdAndAcronimoIgnoreCase(Long departamentoId, String acronimo);

	boolean existsByDepartamentoIdAndAcronimoIgnoreCaseAndIdNot(Long departamentoId, String acronimo, Long id);

}
