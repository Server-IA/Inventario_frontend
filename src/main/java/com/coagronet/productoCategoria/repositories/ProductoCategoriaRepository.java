package com.coagronet.productoCategoria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.productoCategoria.ProductoCategoria;

@Repository
public interface ProductoCategoriaRepository extends JpaRepository<ProductoCategoria, Long> {

    @Query("SELECT p FROM ProductoCategoria p WHERE p.empresa.id = :empresaId AND p.estado.id != :estadoId")
    List<ProductoCategoria> findByEmpresaAndEstadoNot(@Param("empresaId") Long empresaId,
            @Param("estadoId") Integer estadoId);

    @Query("SELECT p FROM ProductoCategoria p WHERE p.id = :id  AND p.empresa.id = :empresaId AND p.estado.id != :estadoId")
    ProductoCategoria findByIdAndEmpresaAndEstadoNot(@Param("id") Long id, @Param("empresaId") Long empresaId,
            @Param("estadoId") Integer estadoId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductoCategoria p WHERE p.id = :id AND p.empresa.id = :empresaId AND p.estado.id != :estadoId")
    boolean existsByIdAndEmpresaAndEstadoNot(@Param("id") Long id, @Param("empresaId") Long empresaId,
            @Param("estadoId") Integer estadoId);

}
