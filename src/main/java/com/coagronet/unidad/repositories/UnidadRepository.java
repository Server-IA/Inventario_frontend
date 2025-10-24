package com.coagronet.unidad.repositories;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.unidad.Unidad;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {

	Page<Unidad> findAllByOrderById(Pageable pageable);

}
