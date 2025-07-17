package com.coagronet.empresa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.empresa.Empresa;

import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Page<Empresa> findByEstadoNot(Integer estado, Pageable pageable);

    Optional<Empresa> findByEmp(String empLogoHash);
}
