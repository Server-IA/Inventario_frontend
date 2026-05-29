package com.coagronet.departamento.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.departamento.Departamento;

@Repository
public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {

	List<Departamento> findAllByOrderByIdAsc();

	List<Departamento> findByPaisIdOrderByIdAsc(Long paisId);

	@Query("""
			SELECT d
			FROM Departamento d
			WHERE (:paisId IS NULL OR d.pais.id = :paisId)
			  AND (:nombre IS NULL OR :nombre = '' OR LOWER(d.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')))
			  AND (:codigo IS NULL OR d.codigo = :codigo)
			  AND (:acronimo IS NULL OR :acronimo = '' OR LOWER(d.acronimo) LIKE LOWER(CONCAT('%', :acronimo, '%')))
			  AND (:estadoId IS NULL OR d.estado.id = :estadoId)
			ORDER BY d.id ASC
			""")
	List<Departamento> findAllWithFilters(@Param("paisId") Long paisId, @Param("nombre") String nombre,
			@Param("codigo") Integer codigo, @Param("acronimo") String acronimo, @Param("estadoId") Long estadoId);

	List<Departamento> findByPaisIdAndEstadoIdNotOrderByIdAsc(Long paisId, Long estadoId);

	boolean existsByPaisIdAndNombreIgnoreCase(Long paisId, String nombre);

	boolean existsByPaisIdAndNombreIgnoreCaseAndIdNot(Long paisId, String nombre, Long id);

	boolean existsByPaisIdAndCodigo(Long paisId, Integer codigo);

	boolean existsByPaisIdAndCodigoAndIdNot(Long paisId, Integer codigo, Long id);

	boolean existsByPaisIdAndAcronimoIgnoreCase(Long paisId, String acronimo);

	boolean existsByPaisIdAndAcronimoIgnoreCaseAndIdNot(Long paisId, String acronimo, Long id);

}
