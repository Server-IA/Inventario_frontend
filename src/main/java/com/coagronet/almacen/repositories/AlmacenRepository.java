package com.coagronet.almacen.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.almacen.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

	Optional<Almacen> findByIdAndEmpresaId(Long id, Long empresaId);

	Page<Almacen> findByEmpresaIdOrderByIdAsc(Long empresaId, Pageable pageable);

	List<Almacen> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}
