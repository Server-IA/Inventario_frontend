package com.coagronet.pasantia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.pasantia.entity.InventarioProgresoId;

@Repository
public interface PasantiaInventarioProgresoRepository
        extends JpaRepository<com.coagronet.pasantia.entity.InventarioProgreso, InventarioProgresoId> {
    List<com.coagronet.pasantia.entity.InventarioProgreso> findByIdInventarioId(Long inventarioId);
}
