package com.coagronet.actividadOcupacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.actividadOcupacion.ActividadOcupacion;

public interface ActividadOcupacionRepository extends JpaRepository<ActividadOcupacion, Integer> {

    List<ActividadOcupacion> findAllByOrderByIdAsc();

}
