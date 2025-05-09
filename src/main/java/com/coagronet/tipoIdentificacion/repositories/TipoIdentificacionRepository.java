package com.coagronet.tipoIdentificacion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoIdentificacion.TipoIdentificacion;

@Repository
public interface TipoIdentificacionRepository extends JpaRepository<TipoIdentificacion, Long> {

    List<TipoIdentificacion> findByEstadoIdNotOrderByIdAsc(
            Long estadoId);

    Optional<TipoIdentificacion> findById(
            Long id);

    boolean existsById(
            Long id);

    boolean existsByIdAndEstadoIdNot(
            Long id,
            Long estadoId);

}
