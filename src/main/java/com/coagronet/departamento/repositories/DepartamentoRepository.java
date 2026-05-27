package com.coagronet.departamento.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.departamento.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

	List<Departamento> findAllByOrderByIdAsc();

	List<Departamento> findByPaisIdOrderByIdAsc(Long paisId);

	List<Departamento> findByPaisIdAndEstadoIdNotOrderByIdAsc(Long paisId, Long estadoId);

	boolean existsByPaisIdAndNombreIgnoreCase(Long paisId, String nombre);

	boolean existsByPaisIdAndNombreIgnoreCaseAndIdNot(Long paisId, String nombre, Long id);

	boolean existsByPaisIdAndCodigo(Long paisId, Integer codigo);

	boolean existsByPaisIdAndCodigoAndIdNot(Long paisId, Integer codigo, Long id);

	boolean existsByPaisIdAndAcronimoIgnoreCase(Long paisId, String acronimo);

	boolean existsByPaisIdAndAcronimoIgnoreCaseAndIdNot(Long paisId, String acronimo, Long id);

}
