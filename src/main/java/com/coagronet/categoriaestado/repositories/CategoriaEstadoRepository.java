package com.coagronet.categoriaestado.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.categoriaestado.CategoriaEstado;

@Repository
public interface CategoriaEstadoRepository extends JpaRepository<CategoriaEstado, Long> {

	List<CategoriaEstado> findAllByOrderByIdAsc();

}
