package com.inventario.pasantia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasantiaSubseccionRepository extends JpaRepository<com.inventario.pasantia.entity.Subseccion, Long> {
}
