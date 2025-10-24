package com.coagronet.tipounidad.repositories;

import com.coagronet.tipounidad.TipoUnidad;
import com.coagronet.unidad.Unidad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TipoUnidadRepository extends JpaRepository<TipoUnidad, Long> {



}
