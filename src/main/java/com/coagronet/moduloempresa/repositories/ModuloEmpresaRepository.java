package com.coagronet.moduloempresa.repositories;

import com.coagronet.empresa.Empresa;
import com.coagronet.modulo.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.moduloempresa.ModuloEmpresa;

public interface ModuloEmpresaRepository extends JpaRepository<ModuloEmpresa, Long> {
    // Validar si ya existe la asignación
    boolean existsByEmpresaAndModulo(Empresa empresa, Modulo modulo);
}
