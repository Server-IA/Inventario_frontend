package com.inventario.presentacion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventario.presentacion.Presentacion;

import java.util.List;
import java.util.Optional;

public interface PresentacionRepository extends JpaRepository<Presentacion, Long> {

	List<Presentacion> findByEmpresaIdOrderByIdAsc(Long empresaId);

	Optional<Presentacion> findByIdAndEmpresaId(Long id, Long empresaId);

}
