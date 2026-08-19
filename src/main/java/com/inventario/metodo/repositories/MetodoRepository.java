package com.inventario.metodo.repositories;

import com.inventario.metodo.Metodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository para la entidad Metodo (GET, POST, PUT, DELETE, PATCH, etc).
 */
@Repository
public interface MetodoRepository extends JpaRepository<Metodo, Long> {

    /**
     * Obtiene el nombre del método por su ID.
     * Útil para obtener "GET", "POST", etc.
     */
    @Query("SELECT m.nombre FROM Metodo m WHERE m.id = :metodoId")
    String findNombreById(@Param("metodoId") Long metodoId);
}
