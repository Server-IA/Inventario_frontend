package com.coagronet.sede.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.coagronet.sede.Sede;

public interface SedeRepository extends JpaRepository<Sede, Long> {

        @Query("SELECT s FROM Sede s WHERE s.empresa.id = :empresaId AND s.estado.id != :estadoId ORDER BY s.nombre ASC")
        List<Sede> findByEmpresaIdAndEstadoIdNot(@Param("empresaId") Long empresaId,
                        @Param("estadoId") Integer estadoId);

        @Query("SELECT s FROM Sede s WHERE s.id = :id  AND s.empresa.id = :empresaId AND s.estado.id != :estadoId")
        Optional<Sede> findByIdAndEmpresaIdAndEstadoIdNot(@Param("id") Long id, @Param("empresaId") Long empresaId,
                        @Param("estadoId") Integer estadoId);

        @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Sede s WHERE s.id = :id AND s.empresa.id = :empresaId AND s.estado.id != :estadoId")
        boolean existsByIdAndEmpresaIdAndEstadoIdNot(@Param("id") Long id, @Param("empresaId") Long empresaId,
                        @Param("estadoId") Integer estadoId);

}
