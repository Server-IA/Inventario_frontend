package com.inventario.moduloempresa.repositories;

import com.inventario.empresa.Empresa;
import com.inventario.modulo.Modulo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;

import com.inventario.moduloempresa.ModuloEmpresa;

public interface ModuloEmpresaRepository extends JpaRepository<ModuloEmpresa, Long> {
    // Validar si ya existe la asignación
    boolean existsByEmpresaAndModulo(Empresa empresa, Modulo modulo);

    @Query("""
            select me.modulo.id
            from ModuloEmpresa me
            where me.empresa.id = :empresaId
              and me.modulo.id in :moduloIds
            """)
    Set<Long> findModuloIdsByEmpresaIdAndModuloIdIn(@Param("empresaId") Long empresaId,
                                                     @Param("moduloIds") Set<Long> moduloIds);
}
