package com.inventario.rolpermiso.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventario.permiso.Permiso;
import com.inventario.rolpermiso.RolPermiso;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    @Query("""
        SELECT rp.permiso 
        FROM RolPermiso rp 
        WHERE rp.empresaRol.id = :empresaRolId 
        AND rp.estado.id = 1
    """)
    List<Permiso> findPermisosByEmpresaRolId(@Param("empresaRolId") Long empresaRolId);

    boolean existsByEmpresaRolIdAndPermisoId(Long empresaRolId, Long permisoId);

    RolPermiso findByEmpresaRolIdAndPermisoId(Long empresaRolId, Long permisoId);

    @Modifying
    @Query("""
        DELETE FROM RolPermiso rp
        WHERE rp.empresaRol.id = :empresaRolId
        AND rp.permiso.id IN :permisoIds
    """)
    void deleteByEmpresaRolIdAndPermisoIds(
        @Param("empresaRolId") Long empresaRolId,
        @Param("permisoIds") List<Long> permisoIds
    );

    // Método proveniente de la rama: develop
    // Optimizado: Uso de Text Blocks para proyección escalar (eficiencia de memoria)
    @Query("""
        SELECT rp.permiso.id 
        FROM RolPermiso rp 
        WHERE rp.empresaRol.id = :empresaRolId 
        AND rp.permiso.id IN :permisoIds
    """)
    Set<Long> findPermisoIdsByEmpresaRolIdAndPermisoIdIn(
        @Param("empresaRolId") Long empresaRolId,
        @Param("permisoIds") List<Long> permisoIds
    );

    // Método proveniente de la rama: feature/rf-025-1-gestion-kardex
    // Optimizado: Uso de Text Blocks. Previene N+1 mediante JOIN FETCH explícito.
    @Query("""
        SELECT rp 
        FROM RolPermiso rp 
        JOIN FETCH rp.permiso p 
        JOIN rp.empresaRol er 
        WHERE er.rol.id = :rolId 
        AND (er.empresa.id = :empresaId OR :empresaId IS NULL) 
        AND rp.estado.id = 1 
        AND er.estado.id = 1
    """)
    List<RolPermiso> findByRolIdAndEmpresaIdWithPermisos(
        @Param("rolId") Long rolId,
        @Param("empresaId") Long empresaId
    );
}