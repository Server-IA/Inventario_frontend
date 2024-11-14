package com.coagronet.tipoSede.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.tipoSede.TipoSede;

@Repository
public interface TipoSedeRepository extends JpaRepository<TipoSede, Integer> {

    @Query("SELECT t FROM TipoSede t WHERE t.estado.id != :estadoId")
    Page<TipoSede> findByEstadoNot(@Param("estadoId") Integer estadoId, Pageable pageable);

    @Query("SELECT t FROM TipoSede t WHERE t.id = :id AND t.estado.id != :estadoId")
    TipoSede findByIdAndEstadoNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TipoSede t WHERE t.id = :id AND t.estado.id != :estadoId")
    boolean existsByIdAndEstadoNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

}
