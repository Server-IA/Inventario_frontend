package com.coagronet.productoPresentacion.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.productoPresentacion.ProductoPresentacion;

@Repository
public interface ProductoPresentacionRepository extends JpaRepository<ProductoPresentacion, Integer> {

    @Query("SELECT p FROM ProductoPresentacion p WHERE p.id = :id AND p.estado.id != :estadoId")
    ProductoPresentacion findByIdAndEstadoNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

    @Query("SELECT p FROM ProductoPresentacion p WHERE p.estado.id != :estadoId")
    Page<ProductoPresentacion> findByEstadoNot(@Param("estadoId") Integer estadoId, Pageable pageable);

    Page<ProductoPresentacion> findByProductoEmpresaIdAndEstadoIdNot(
            Long empresaId,
            Integer estadoId,
            Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM ProductoPresentacion p WHERE p.id = :id AND p.estado.id != :estadoId")
    boolean existsByIdAndEstadoNot(@Param("id") Integer id, @Param("estadoId") Integer estadoId);

    boolean existsByIdAndProductoEmpresaId(
            Integer id,
            Long empresaId);
}
