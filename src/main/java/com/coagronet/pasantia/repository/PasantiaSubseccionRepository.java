package com.coagronet.pasantia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasantiaSubseccionRepository extends JpaRepository<com.coagronet.pasantia.entity.Subseccion, Long> {
}
