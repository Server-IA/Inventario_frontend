package com.coagronet.persona.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.persona.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    Page<Persona> findByEstadoIdNot(Integer estadoId, Pageable pageable);

    Optional<Persona> findById(Long id);

    boolean existsById(Long id);

}
