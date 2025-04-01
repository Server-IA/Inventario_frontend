package com.coagronet.sede.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.sede.Sede;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long> {

    List<Sede> findByEmpresaId(
            Long empresaId);

    List<Sede> findByEmpresaIdAndEstadoIdNot(
            Long empresaId,
            Integer estadoId);

    Optional<Sede> findByIdAndEmpresaId(
            Long id,
            Long empresaId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sede s WHERE s.id = :id AND s.empresa.id = :empresaId AND s.estado.id != :estadoId")
    boolean existsByIdAndEmpresaIdAndEstadoIdNot(@Param("id") Long id, @Param("empresaId") Long empresaId,
            @Param("estadoId") Integer estadoId);

}
