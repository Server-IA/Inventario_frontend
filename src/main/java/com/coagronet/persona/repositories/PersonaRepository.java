package com.coagronet.persona.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coagronet.persona.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

	Page<Persona> findByEstadoIdNot(Long estadoId, Pageable pageable);

	Optional<Persona> findById(Long id);

	boolean existsById(Long id);

	Boolean existsByTipoIdentificacion_IdAndIdentificacionAndEstado_Id(Long tipoIdentificacionId, String identificacion,
			Long estadoId);

	Boolean existsByEmailAndEstado_Id(String email, Long estadoId);

}
