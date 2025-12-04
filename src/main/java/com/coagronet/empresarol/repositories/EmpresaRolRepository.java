package com.coagronet.empresarol.repositories;

import com.coagronet.empresarol.EmpresaRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRolRepository extends JpaRepository<EmpresaRol, Long> {

    List<EmpresaRol>findByEmpresaId(Long empresaId);

    Optional<EmpresaRol>findByIdAndEmpresaId(Long id, Long empresaId);

}
