package com.coagronet.pais.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.pais.Pais;

@Repository
public interface PaisRepository extends JpaRepository<Pais, Long> {

	List<Pais> findAllByOrderByIdAsc();

	List<Pais> findByEstadoIdNotOrderByIdAsc(Long estadoId);

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

	boolean existsByCodigo(Long codigo);

	boolean existsByCodigoAndIdNot(Long codigo, Long id);

	boolean existsByAcronimoIgnoreCase(String acronimo);

	boolean existsByAcronimoIgnoreCaseAndIdNot(String acronimo, Long id);

}
