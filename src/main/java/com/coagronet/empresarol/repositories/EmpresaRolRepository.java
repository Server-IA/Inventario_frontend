package com.coagronet.empresarol.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.coagronet.empresarol.EmpresaRol;
import com.coagronet.rol.Rol;

@Repository
public interface EmpresaRolRepository extends JpaRepository<EmpresaRol, Long> {

  List<EmpresaRol> findByEmpresaId(Long empresaId);

  Optional<EmpresaRol> findByIdAndEmpresaId(Long id, Long empresaId);

  @Query("""
      select er.rol
      from EmpresaRol er
      where er.empresa.id = :empresaId
        and er.rol.id = :rolId
      """)
  Optional<Rol> findRolByEmpresaIdAndRolId(Long empresaId, Long rolId);

  @Query("""
      select er.rol
      from EmpresaRol er
      where er.empresa.id = :empresaId
        and er.rol.id = :rolId
        and er.estado.id = :estadoId
      """)
  Optional<Rol> findRolByEmpresaIdAndRolIdAndEstadoId(Long empresaId, Long rolId, Long estadoId);

  Optional<EmpresaRol> findByEmpresaIdAndRolIdAndEstadoId(Long empresaId, Long rolId, Long estadoId);

  boolean existsByEmpresaIdAndRolId(Long empresaId, Long rolId);
}
