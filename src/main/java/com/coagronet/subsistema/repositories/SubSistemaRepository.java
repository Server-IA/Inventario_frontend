package com.coagronet.subsistema.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.coagronet.subsistema.SubSistema;

public interface SubSistemaRepository extends JpaRepository<SubSistema, Long> {

    @Query("select s.nombre, s.icon from SubSistema s where s.estado.id = :estadoId and s.tipoAplicacion.id = :tipoAplicacionId order by s.id desc")
    SubSistema findByEstadoIdAndTipoAplicacionId(Long estadoId, Long tipoAplicacionId);

}
