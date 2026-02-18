package com.coagronet.rolpermiso.repositories;

import com.coagronet.permiso.Permiso;
import com.coagronet.rolpermiso.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {

    @Query("SELECT rp.permiso FROM RolPermiso rp WHERE rp.empresaRol.id = :empresaRolId AND rp.estado.id = 1")
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

}
