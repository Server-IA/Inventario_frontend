package com.coagronet.almacen.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.almacen.Almacen;

public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

	Optional<Almacen> findByIdAndEmpresaId(Long id, Long empresaId);

	List<Almacen> findByEmpresaIdOrderByIdAsc(Long empresaId);

	List<Almacen> findByEmpresaIdAndEstadoIdNotOrderByIdAsc(Long empresaId, Long estadoId);

}
