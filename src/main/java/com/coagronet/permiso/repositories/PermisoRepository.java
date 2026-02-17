package com.coagronet.permiso.repositories;

import com.coagronet.permiso.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {

    @Query("""
        SELECT DISTINCT p.autoridad
        FROM UsuarioRol ur
        JOIN EmpresaRol er 
            ON er.empresa.id = ur.empresa.id 
           AND er.rol.id = ur.rol.id
        JOIN RolPermiso rp 
            ON rp.empresaRol.id = er.id
        JOIN Permiso p 
            ON p.id = rp.permiso.id
        WHERE ur.usuario.id = :usuarioId
          AND ur.empresa.id = :empresaId
          AND ur.estado.id = 1
          AND rp.estado.id = 1
          AND p.estado.id = 1
          AND (ur.finalizaContratoEn IS NULL OR ur.finalizaContratoEn > CURRENT_TIMESTAMP)
    """)
    List<String> findPermisosByUsuarioAndEmpresa(
            @Param("usuarioId") Long usuarioId,
            @Param("empresaId") Long empresaId
    );
}
