package com.coagronet.grupo.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.coagronet.empresa.Empresa;
import com.coagronet.grupo.Grupo;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    Page<Grupo> findById(Long id, PageRequest pageRequest);

    List<Grupo> findByEstadoNotAndEmpresa(Integer estado, Empresa empresa, Sort sort);

    Optional<Grupo> findByIdAndEmpresaIdAndEstadoIdNot(Long id, Long empresaId, Long estadoId);

    @Query(value = "select * from grupo g where gru_id = :grupoId and gru_empresa_id = :empresaId and gru_estado_id <> :estadoId", nativeQuery = true)
    Optional<Grupo> buscarPorIdYEmpresaIdYEstadoEsDiferenteDeInactivo(@Param("grupoId") Long grupoId, @Param("empresaId") Long empresaId, @Param("estadoId") Long estadoId);
}
