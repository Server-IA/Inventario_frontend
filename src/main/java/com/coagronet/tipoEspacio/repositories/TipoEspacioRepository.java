package com.coagronet.tipoEspacio.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoEspacio.TipoEspacio;

@Repository
public interface TipoEspacioRepository extends JpaRepository<TipoEspacio, Integer> {

    @Query("SELECT t FROM TipoEspacio t WHERE t.estado.id != :estadoId")
    Page<TipoEspacio> findByEstadoNot(@Param("estadoId") Integer estadoId, Pageable pageable);

    @Query("SELECT t FROM TipoEspacio t WHERE t.id = :id AND t.estado.id != :estadoId")
    TipoEspacio findByIdAndEstadoNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TipoEspacio t WHERE t.id = :id AND t.estado.id != :estadoId")
    boolean existsByIdAndEstadoNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

}
