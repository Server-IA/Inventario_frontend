package com.coagronet.tipoProduccion.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoProduccion.TipoProduccion;

@Repository
public interface TipoProduccionRepository extends JpaRepository<TipoProduccion, Integer> {

    @Query(value = "SELECT * FROM public.tipo_produccion\n " +
            "WHERE tip_estado <> 2\n" +
            "ORDER BY tip_id ASC", nativeQuery = true)
    List<TipoProduccion> listarTipoProduccion();

    Optional<TipoProduccion> findByIdAndEstadoNot(Integer id, Integer estado);

}
