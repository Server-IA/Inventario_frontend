package com.coagronet.marca.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.marca.Marca;

@Repository
public interface MarcaRepository extends JpaRepository<Marca, Long> {

    @Query("SELECT m FROM Marca m WHERE m.estado.id != :estadoId")
    List<Marca> findByEstadoNot(@Param("estadoId") Integer estadoId);

    @Query("SELECT m FROM Marca m WHERE m.id = :id AND m.estado.id != :estadoId")
    Marca findByIdAndEstadoNot(@Param("id") Long id, @Param("estadoId") Integer estadoId);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM Marca m WHERE m.id = :id AND m.estado.id != :estadoId")
    boolean existsByIdAndEstadoNot(@Param("id") Long id, @Param("estadoId") Integer estadoId);

}
