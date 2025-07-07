package com.coagronet.estadoCategoria.repositories;

import com.coagronet.estadoCategoria.EstadoCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoCategoriaRepository extends JpaRepository<EstadoCategoria, Long> {

}
