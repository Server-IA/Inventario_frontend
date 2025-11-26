package com.coagronet.rol.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coagronet.rol.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {

	Optional<Rol> findByNombre(String nombre);

	boolean existsByNombreIgnoreCase(String nombre);

}