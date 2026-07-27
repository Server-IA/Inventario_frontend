package com.coagronet.pasantia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PasantiaInventarioRepository extends JpaRepository<com.coagronet.pasantia.entity.Inventario, Long> {

    @Query("SELECT i FROM com.coagronet.pasantia.entity.Inventario i " +
            "JOIN FETCH i.subseccion sub " +
            "JOIN FETCH sub.seccion sec " +
            "JOIN FETCH i.estado est " +
            "WHERE i.usuarioAsignadoId = :usuarioId")
    List<com.coagronet.pasantia.entity.Inventario> findByUsuarioAsignadoIdWithDetails(
            @Param("usuarioId") Long usuarioId);
}
